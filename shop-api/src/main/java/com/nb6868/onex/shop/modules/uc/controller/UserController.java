package com.nb6868.onex.shop.modules.uc.controller;

import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.nb6868.onex.common.auth.AuthProps;
import com.nb6868.onex.shop.modules.uc.service.UserService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 */
@RestController("UcUserController")
@RequestMapping("/uc/user")
@Validated
@Api(tags = "用户")
@ApiSupport(order = 10)
@Slf4j
public class UserController {

    @Autowired
    private AuthProps authProps;
    @Autowired
    private UserService userService;

    /**
     * 微信登录调整 https://developers.weixin.qq.com/community/develop/doc/000cacfa20ce88df04cb468bc52801
     */
    /*@PostMapping("/wxMaLoginByCodeAndUserInfo")
    @AccessControl("/wxMaLoginByCodeAndUserInfo")
    @ApiOperation(value = "微信小程序用户信息授权登录", notes = "注意,wx.getUserProfile/wx.getUserInfo放在wx.login之前会偶发解密失败")
    @LogOperation(value = "微信小程序用户信息授权登录", type = "login")
    public Result<?> wxMaLoginByCodeAndUserInfo(@Validated @RequestBody OauthWxMaLoginByCodeAndUserInfoRequest request) throws WxErrorException{
        // 获得登录配置
        AuthProps.Config loginConfig = authProps.getConfigs().get("SHOP_WXMA_PHONE");
        AssertUtils.isNull(loginConfig, ErrorCode.UNKNOWN_LOGIN_TYPE);

        // 微信登录
        WxMaService wxService = WechatMaPropsConfig.getService(request.getWechatMaConfigType());
        WxMaJscode2SessionResult jscode2SessionResult = wxService.getUserService().getSessionInfo(request.getCode());

        // 用户信息校验
        if (!wxService.getUserService().checkUserInfo(jscode2SessionResult.getSessionKey(), request.getRawData(), request.getSignature())) {
            return new Result<>().error(ErrorCode.WX_API_ERROR, "user check failed");
        }
        // 解密用户信息
        WxMaUserInfo userInfo = wxService.getUserService().getUserInfo(jscode2SessionResult.getSessionKey(), request.getEncryptedData(), request.getIv());
        // openid和unionid从WxMaJscode2SessionResult获取
        return new Result<>().success(userInfo);
    }

    @PostMapping("/wxMaLoginByPhone")
    @AccessControl("/wxMaLoginByPhone")
    @ApiOperation(value = "微信小程序手机号授权登录")
    @LogOperation(value = "微信小程序手机号授权登录", type = "login")
    public Result<Dict> wxMaLoginByPhone(@Validated(value = {DefaultGroup.class}) @RequestBody OauthWxMaLoginByCodeAndPhone request) {
        AuthProps.Config loginProps = authProps.getConfigs().get("SHOP_WXMA_PHONE");
        AssertUtils.isNull(loginProps, ErrorCode.UNKNOWN_LOGIN_TYPE);

        // 微信登录(小程序)
        WxMaService wxService = WechatMaPropsConfig.getService(request.getWechatMaConfigType());
        WxMaJscode2SessionResult jscode2SessionResult;
        try {
            jscode2SessionResult = wxService.getUserService().getSessionInfo(request.getCode());
        } catch (WxErrorException e) {
            log.error(e.getMessage());
            return new Result<Dict>().error("获取微信信息失败,Error=" + e.getError().getErrorCode());
        }
        // 解密用户手机号
        WxMaPhoneNumberInfo phoneNumberInfo = wxService.getUserService().getPhoneNoInfo(jscode2SessionResult.getSessionKey(), request.getEncryptedData(), request.getIv());
        return null;
    }

    @GetMapping("userInfo")
    @ApiOperation(value = "登录用户信息")
    @LogOperation(value = "登录用户信息")
    public Result<UserDTO> userInfo() {
        UserEntity entity = userService.getById(SecurityUser.getUserId());
        AssertUtils.isNull(entity, ErrorCode.DB_RECORD_EXISTS);
        // 转换成dto
        UserDTO dto = ConvertUtils.sourceToTarget(entity, UserDTO.class);
        return new Result<UserDTO>().success(dto);
    }*/

}
