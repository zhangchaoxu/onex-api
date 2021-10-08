package com.nb6868.onex.api.modules.uc.controller;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.URLUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.nb6868.onex.api.modules.msg.MsgConst;
import com.nb6868.onex.api.modules.msg.dto.MailSendRequest;
import com.nb6868.onex.api.modules.msg.service.MailLogService;
import com.nb6868.onex.common.annotation.AccessControl;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.api.modules.uc.UcConst;
import com.nb6868.onex.api.modules.uc.dto.*;
import com.nb6868.onex.api.modules.uc.entity.UserEntity;
import com.nb6868.onex.api.modules.uc.entity.UserOauthEntity;
import com.nb6868.onex.api.modules.uc.service.AuthService;
import com.nb6868.onex.api.modules.uc.service.TokenService;
import com.nb6868.onex.api.modules.uc.service.UserOauthService;
import com.nb6868.onex.api.modules.uc.service.UserService;
import com.nb6868.onex.common.auth.AuthProps;
import com.nb6868.onex.common.dingtalk.DingTalkApi;
import com.nb6868.onex.common.dingtalk.GetUserInfoByCodeResponse;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.pojo.Const;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.util.ConvertUtils;
import com.nb6868.onex.common.util.JacksonUtils;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.common.validator.ValidatorUtils;
import com.nb6868.onex.common.validator.group.AddGroup;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import com.nb6868.onex.common.wechat.WechatMaPropsConfig;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 认证授权相关接口
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController
@RequestMapping("/uc/auth")
@AccessControl("/uc/auth/**")
@Validated
@Api(tags = "用户认证")
public class AuthController {

    @Autowired
    private TokenService tokenService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserOauthService userOauthService;
    @Autowired
    private AuthService authService;
    @Autowired
    private MailLogService mailLogService;

    @GetMapping("getLoginSettings")
    @ApiOperation("获得登录设置")
    public Result<?> getLoginSettings(@RequestParam String type) {
        AuthProps.Settings loginSettings = authService.getLoginSettings(type);
        AssertUtils.isNull(loginSettings, "未定义该类型");

        return new Result<>().success(loginSettings);
    }

    @GetMapping("getLoginConfig")
    @ApiOperation("获得登录配置")
    public Result<?> getLoginConfig(@RequestParam String type) {
        AuthProps.Config loginConfig = authService.getLoginConfig(type);
        AssertUtils.isNull(loginConfig, "未定义该类型");

        return new Result<>().success(loginConfig);
    }

    @PostMapping("sendLoginCode")
    @ApiOperation("发送登录验证码消息")
    @LogOperation("发送登录验证码消息")
    public Result<?> sendLoginCode(@Validated(value = {AddGroup.class}) @RequestBody MailSendRequest dto) {
        // 只允许发送CODE_开头的模板
        AssertUtils.isFalse(dto.getTplCode().startsWith(MsgConst.SMS_CODE_TPL_PREFIX), "只支持" + MsgConst.SMS_CODE_TPL_PREFIX + "类型模板发送");
        boolean flag = mailLogService.send(dto);
        if (flag) {
            return new Result<>();
        }
        return new Result<>().error("消息发送失败");
    }

    @PostMapping("login")
    @ApiOperation(value = "登录")
    @LogOperation(value = "登录", type = "login")
    public Result<?> login(@Validated(value = {DefaultGroup.class}) @RequestBody LoginRequest loginRequest) {
        // 获得登录配置
        AuthProps.Config loginConfig = authService.getLoginConfig(loginRequest.getType());
        AssertUtils.isNull(loginConfig, ErrorCode.UNKNOWN_LOGIN_TYPE);

        UserEntity user = authService.login(loginRequest, loginConfig);

        // 登录成功
        Dict dict = Dict.create();
        dict.set("tokenKey", UcConst.TOKEN_HEADER);
        dict.set("token", tokenService.createToken(user, loginConfig));
        dict.set("user", ConvertUtils.sourceToTarget(user, UserDTO.class));
        return new Result<>().success(dict);
    }

    @SneakyThrows
    @PostMapping("loginEncrypt")
    @ApiOperation(value = "加密登录")
    @LogOperation(value = "加密登录", type = "login")
    public Result<?> loginEncrypt(@RequestBody String loginEncrypted) {
        // 密文->urldecode->base64解码->aes解码->原明文->json转实体
        LoginRequest loginRequest = JacksonUtils.jsonToPojo(SecureUtil.aes(Const.AES_KEY.getBytes()).decryptStr(Base64.decodeStr(URLUtil.decode(loginEncrypted))), LoginRequest.class);
        // 效验数据
        ValidatorUtils.validateEntity(loginRequest, DefaultGroup.class);
        return login(loginRequest);
    }

    @PostMapping("register")
    @ApiOperation(value = "注册")
    public Result<?> register(@Validated @RequestBody RegisterRequest request) {
        UserEntity userEntity = userService.register(request);
        UserDTO userDTO = ConvertUtils.sourceToTarget(userEntity, UserDTO.class);
        return new Result<>().success(userDTO);
    }

    @Deprecated
    @PostMapping("/wxMaLoginByCodeAndUserInfo")
    @ApiOperation("Oauth授权登录")
    @LogOperation(value = "Oauth授权登录", type = "login")
    public Result<?> wxMaLoginByCodeAndUserInfo(@Validated @RequestBody OauthWxMaLoginByCodeAndUserInfoRequest request) throws WxErrorException {
        // 获得登录配置
        AuthProps.Config loginConfig = authService.getLoginConfig(request.getType());
        AssertUtils.isNull(loginConfig, ErrorCode.UNKNOWN_LOGIN_TYPE);

        // 微信登录
        WxMaService wxService = WechatMaPropsConfig.getService(request.getType());
        WxMaJscode2SessionResult jscode2SessionResult = wxService.getUserService().getSessionInfo(request.getCode());

        // 用户信息校验
        if (!wxService.getUserService().checkUserInfo(jscode2SessionResult.getSessionKey(), request.getRawData(), request.getSignature())) {
            return new Result<>().error(ErrorCode.WX_API_ERROR, "user check failed");
        }
        // 解密用户信息
        WxMaUserInfo userInfo = wxService.getUserService().getUserInfo(jscode2SessionResult.getSessionKey(), request.getEncryptedData(), request.getIv());

        // 更新或者插入Oauth表
        UserOauthEntity userOauth = userOauthService.saveOrUpdateByWxMaUserInfo(wxService.getWxMaConfig().getAppid(), userInfo, jscode2SessionResult.getOpenid(), jscode2SessionResult.getUnionid());
        // 用户
        UserEntity user = null;
        if (userOauth.getUserId() != null) {
            user = userService.getById(userOauth.getUserId());
            if (null == user) {
                // 如果用户空了,同时结束所有绑定关系
                userOauthService.unbindByUserId(userOauth.getUserId());
            }
        }
        if (user == null) {
            // 根据业务提示错误或者自动创建用户
            return new Result<>().error(ErrorCode.OAUTH_NOT_BIND_ERROR);
        }
        // 登录成功
        Dict dict = Dict.create();
        dict.set("tokenKey", UcConst.TOKEN_HEADER);
        dict.set("token", tokenService.createToken(user, loginConfig));
        dict.set("user", ConvertUtils.sourceToTarget(user, UserDTO.class));
        return new Result<>().success(dict);
    }

    @PostMapping("/wxMaLoginByCode")
    @ApiOperation("Oauth微信小程序授权登录")
    @LogOperation(value = "Oauth微信小程序授权登录", type = "login")
    public Result<?> wxMaLoginByCode(@Validated @RequestBody OauthLoginByCodeRequest request) throws WxErrorException {
        // 获得登录配置
        AuthProps.Config loginConfig = authService.getLoginConfig(request.getType());
        AssertUtils.isNull(loginConfig, ErrorCode.UNKNOWN_LOGIN_TYPE);

        // 微信登录(小程序)
        WxMaService wxService = WechatMaPropsConfig.getService(request.getType());
        WxMaJscode2SessionResult jscode2SessionResult = wxService.getUserService().getSessionInfo(request.getCode());
        // 更新或者插入Oauth表
        UserOauthEntity userOauth = userOauthService.saveOrUpdateByWxMaJscode2SessionResult(wxService.getWxMaConfig().getAppid(), jscode2SessionResult);
        // 用户
        UserEntity user = null;
        if (userOauth.getUserId() != null) {
            user = userService.getById(userOauth.getUserId());
            if (null == user) {
                // 如果用户空了,同时结束所有绑定关系
                userOauthService.unbindByUserId(userOauth.getUserId());
            }
        }
        if (user == null) {
            // 根据业务提示错误或者自动创建用户
            return new Result<>().error(ErrorCode.OAUTH_NOT_BIND_ERROR);
        }
        // 登录成功
        Dict dict = Dict.create();
        dict.set("tokenKey", UcConst.TOKEN_HEADER);
        dict.set("token", tokenService.createToken(user, loginConfig));
        dict.set("user", ConvertUtils.sourceToTarget(user, UserDTO.class));
        return new Result<>().success(dict);
    }

    @PostMapping("/wxMaLoginByPhone")
    @ApiOperation("Oauth微信小程序手机号授权登录")
    @LogOperation(value = "Oauth微信小程序手机号授权登录", type = "login")
    public Result<?> wxMaLoginByPhone(@Validated @RequestBody OauthWxMaLoginByCodeAndPhone request) throws WxErrorException {
        // 获得登录配置
        AuthProps.Config loginConfig = authService.getLoginConfig(request.getType());
        AssertUtils.isNull(loginConfig, ErrorCode.UNKNOWN_LOGIN_TYPE);

        // 微信登录(小程序)
        WxMaService wxService = WechatMaPropsConfig.getService(request.getType());
        WxMaJscode2SessionResult jscode2SessionResult = wxService.getUserService().getSessionInfo(request.getCode());
        // 解密用户手机号
        WxMaPhoneNumberInfo phoneNumberInfo = wxService.getUserService().getPhoneNoInfo(jscode2SessionResult.getSessionKey(), request.getEncryptedData(), request.getIv());
        UserEntity user = userService.getOneByColumn("mobile", phoneNumberInfo.getPurePhoneNumber());
        if (user == null) {
            // todo 用户不存在,按照实际业务需求创建用户或者提示用户不存在
            user = new UserEntity();
            user.setMobileArea(phoneNumberInfo.getCountryCode());
            user.setMobile(phoneNumberInfo.getPurePhoneNumber());
            user.setUsername(phoneNumberInfo.getPurePhoneNumber());
            user.setPassword(DigestUtil.bcrypt(phoneNumberInfo.getPurePhoneNumber()));
            user.setState(UcConst.UserStateEnum.ENABLED.value());
            user.setType(UcConst.UserTypeEnum.USER.value());
            userService.save(user);
        }
        // 登录成功
        Dict dict = Dict.create();
        dict.set("tokenKey", UcConst.TOKEN_HEADER);
        dict.set("token", tokenService.createToken(user, loginConfig));
        dict.set("user", ConvertUtils.sourceToTarget(user, UserDTO.class));
        return new Result<>().success(dict);
    }

    /**
     * 钉钉扫码授权登录，通过code登录
     * see https://ding-doc.dingtalk.com/document/app/scan-qr-code-to-log-on-to-third-party-websites
     */
    @PostMapping("/dingtalkLoginByCode")
    @ApiOperation("钉钉扫码授权登录")
    @LogOperation(value = "钉钉扫码授权登录", type = "login")
    public Result<?> dingtalkLoginByCode(@Validated @RequestBody OauthLoginByCodeRequest request) {
        // 获得登录配置
        AuthProps.Config loginConfig = authService.getLoginConfig(request.getType());
        AssertUtils.isNull(loginConfig, ErrorCode.UNKNOWN_LOGIN_TYPE);

        // 1. 根据sns临时授权码获取用户信息
        GetUserInfoByCodeResponse userInfoByCodeResponse = DingTalkApi.getUserInfoByCode("", "", request.getCode());
        if (userInfoByCodeResponse.isSuccess()) {
            // todo 钉钉接口处理流程
            return new Result<>().success(userInfoByCodeResponse.getUser_info());
        } else {
            return new Result<>().error(userInfoByCodeResponse.getErrcode() + ":" + userInfoByCodeResponse.getErrmsg());
        }
    }

}
