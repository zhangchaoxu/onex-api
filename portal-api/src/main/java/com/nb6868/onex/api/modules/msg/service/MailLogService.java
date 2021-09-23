package com.nb6868.onex.api.modules.msg.service;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaSubscribeMessage;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.text.StrSplitter;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.api.util.TemplateUtils;
import com.nb6868.onex.api.modules.msg.MsgConst;
import com.nb6868.onex.api.modules.msg.dao.MailLogDao;
import com.nb6868.onex.api.modules.msg.dto.MailLogDTO;
import com.nb6868.onex.api.modules.msg.dto.MailSendRequest;
import com.nb6868.onex.api.modules.msg.email.EmailUtils;
import com.nb6868.onex.api.modules.msg.entity.MailLogEntity;
import com.nb6868.onex.api.modules.msg.entity.MailTplEntity;
import com.nb6868.onex.api.modules.msg.sms.SmsFactory;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.pojo.Const;
import com.nb6868.onex.common.jpa.DtoService;
import com.nb6868.onex.common.util.JacksonUtils;
import com.nb6868.onex.common.util.WrapperUtils;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.common.wechat.WechatMaPropsConfig;
import com.nb6868.onex.common.wechat.WechatMpPropsConfig;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 邮件发送记录
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class MailLogService extends DtoService<MailLogDao, MailLogEntity, MailLogDTO> {

    @Autowired
    MailTplService mailTplService;
    @Autowired
    EmailUtils emailUtils;

    @Override
    public QueryWrapper<MailLogEntity> getWrapper(String method, Map<String, Object> params) {
        return new WrapperUtils<MailLogEntity>(new QueryWrapper<>(), params)
                .eq("tplCode", "tpl_code")
                .eq("tplType", "tpl_type")
                .like("mailTo", "mail_to")
                .like("mailCc", "mail_cc")
                .eq("state", "state")
                .like("content", "content")
                .getQueryWrapper();
    }

    /**
     * 消费消息
     */
    public boolean consumeById(Long id) {
        return update().eq("id", id).set("consume_state", Const.BooleanEnum.TRUE.value()).update(new MailLogEntity());
    }

    /**
     * 通过模板编码和手机号找最后一次发送记录
     * @param tplCode 模板编码
     * @param mailTo 收件人
     * @return 记录
     */
    public MailLogEntity findLastLogByTplCode(String tplCode, String mailTo) {
        return query().eq("tpl_code", tplCode)
                .eq("mail_to", mailTo)
                .eq("state", Const.BooleanEnum.TRUE.value())
                .eq("consume_state", Const.BooleanEnum.FALSE.value())
                .orderByDesc("create_time")
                .last(Const.LIMIT_ONE)
                .one();
    }

    /**
     * 发送消息
     */
    public boolean send(MailSendRequest request) {
        MailTplEntity mailTpl = mailTplService.getOneByColumn("code", request.getTplCode());
        AssertUtils.isNull(mailTpl, "未定义的消息模板:" + request.getTplCode());
        // 检查消息模板是否有时间限制
        if (mailTpl.getTimeLimit() > 0) {
            // 先校验该收件人是否timeLimit秒内发送过
            MailLogEntity lastMailLog = findLastLogByTplCode(request.getTplCode(), request.getMailTo());
            // 检查限定时间内是否已经发送
            AssertUtils.isTrue(null != lastMailLog && DateUtil.between(DateUtil.date(), lastMailLog.getCreateTime(), DateUnit.SECOND) < mailTpl.getTimeLimit(), ErrorCode.ERROR_REQUEST, "发送请求过于频繁");
        }
        // 判断是否验证码消息类型
        if (mailTpl.getType() == MsgConst.MailTypeEnum.CODE.value()) {
            request.setContentParam(JacksonUtils.pojoToJson(Dict.create().set("code", RandomUtil.randomNumbers(4))));
        }

        if (MsgConst.MailChannelEnum.EMAIL.name().equalsIgnoreCase(mailTpl.getChannel())) {
            // 邮件
            return emailUtils.sendMail(mailTpl, request);
        } else if (MsgConst.MailChannelEnum.SMS.name().equalsIgnoreCase(mailTpl.getChannel())) {
            // 短信
            // 获取短信服务，发送短信
            return SmsFactory.build(mailTpl.getPlatform()).sendSms(mailTpl, request.getMailTo(), request.getContentParam());
        } else if (MsgConst.MailChannelEnum.WX_MP_TEMPLATE.name().equalsIgnoreCase(mailTpl.getChannel())) {
            // 微信模板消息
            WxMpService wxService = WechatMpPropsConfig.getService(mailTpl.getParam());

            // 可能是发送多个
            List<String> openIds = StrSplitter.splitTrim(request.getMailTo(), ',', true);
            for (String openId : openIds) {
                // 构建消息
                WxMpTemplateMessage templateMessage = WxMpTemplateMessage.builder()
                        .toUser(openId)
                        //.templateId(wxProp.getTemplateId())
                        .url("")
                        .build();

                // 封装消息实际内容
                Map<String, Object> contentParam = JacksonUtils.jsonToMap(request.getContentParam());
                String content = TemplateUtils.getTemplateContent("wxTemplateContent", mailTpl.getContent(), contentParam);

                for (String key : contentParam.keySet()) {
                    templateMessage.addData(new WxMpTemplateData(key, contentParam.get(key).toString()));
                }

                Const.ResultEnum state = Const.ResultEnum.FAIL;
                String result;
                try {
                    result = wxService.getTemplateMsgService().sendTemplateMsg(templateMessage);
                    state = Const.ResultEnum.SUCCESS;
                } catch (WxErrorException e) {
                    e.printStackTrace();
                    result = e.getError().getJson();
                }
                // 保存记录
                MailLogEntity mailLog = new MailLogEntity();
                mailLog.setMailTo(openId);
                mailLog.setState(state.value());
                mailLog.setResult(result);
                mailLog.setContent(content);
                mailLog.setTplCode(mailTpl.getCode());
                mailLog.setTplType(mailTpl.getType());
                mailLog.setContentParams(request.getContentParam());
                mailLog.setConsumeState(Const.BooleanEnum.FALSE.value());
                save(mailLog);
            }
            return true;
        } else if (MsgConst.MailChannelEnum.WX_MA_SUBSCRIBE.name().equalsIgnoreCase(mailTpl.getChannel())) {
            // 微信小程序模板消息
            // 微信模板消息
            WxMaService wxService = WechatMaPropsConfig.getService(mailTpl.getParam());

            // 可能是发送多个
            List<String> openIds = StrSplitter.splitTrim(request.getMailTo(), ',', true);
            for (String openId : openIds) {
                // 构建消息
                WxMaSubscribeMessage templateMessage = WxMaSubscribeMessage.builder()
                        .toUser(openId)
                        //.templateId(wxProp.getTemplateId())
                        .build();

                // 封装消息实际内容
                Map<String, Object> contentParam = JacksonUtils.jsonToMap(request.getContentParam());
                String content = TemplateUtils.getTemplateContent("wxTemplateContent", mailTpl.getContent(), contentParam);

                for (String key : contentParam.keySet()) {
                    templateMessage.addData(new WxMaSubscribeMessage.MsgData(key, contentParam.get(key).toString()));
                }

                Const.ResultEnum state = Const.ResultEnum.FAIL;
                String result = "success";
                try {
                    wxService.getMsgService().sendSubscribeMsg(templateMessage);
                    state = Const.ResultEnum.SUCCESS;
                } catch (WxErrorException e) {
                    e.printStackTrace();
                    result = e.getError().getJson();
                }
                // 保存记录
                MailLogEntity mailLog = new MailLogEntity();
                mailLog.setMailTo(openId);
                mailLog.setState(state.value());
                mailLog.setResult(result);
                mailLog.setContent(content);
                mailLog.setTplCode(mailTpl.getCode());
                mailLog.setTplType(mailTpl.getType());
                mailLog.setContentParams(request.getContentParam());
                mailLog.setConsumeState(Const.BooleanEnum.FALSE.value());
                save(mailLog);
            }
            return true;
        } else  {
            return false;
        }
    }

}

