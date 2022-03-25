package com.nb6868.onex.portal.interceptor;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.nb6868.onex.portal.modules.uc.UcConst;
import com.nb6868.onex.common.annotation.WxWebAuth;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.exception.OnexException;
import com.nb6868.onex.common.util.HttpContextUtils;
import com.nb6868.onex.common.wechat.WechatMpPropsConfig;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * 微信页面授权拦截器
 * <p>
 * see {https://developers.weixin.qq.com/doc/offiaccount/OA_Web_Apps/Wechat_webpage_authorization.html}
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Component
public class WxWebAuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (handler instanceof HandlerMethod) {
            WxWebAuth annotation = ((HandlerMethod) handler).getMethodAnnotation(WxWebAuth.class);
            if (annotation != null) {
                // 判断来源是否微信
                String userAgent = request.getHeader("User-Agent");
                // 从session中获取openid
                Object openid = request.getSession(false).getAttribute(UcConst.WX_SESSION_OPEN_ID);
                if (ObjectUtil.isEmpty(openid)) {
                    // 通过是否有code来判断是否回调
                    String code = request.getParameter("code");
                    WxMpService wxService = WechatMpPropsConfig.getService(UcConst.WX_MP);
                    if (StrUtil.isBlank(code)) {
                        String url = HttpContextUtils.getFullUrl(request);
                        String oauth2buildAuthorizationUrl = wxService.getOAuth2Service().buildAuthorizationUrl(url, annotation.scope(), "wx#wechat_redirect");
                        try {
                            response.sendRedirect(oauth2buildAuthorizationUrl);
                            return false;
                        } catch (IOException e) {
                            e.printStackTrace();
                            throw new OnexException(ErrorCode.WX_API_ERROR, "跳转微信授权页面失败");
                        }
                    } else {
                        // 有code,则执行获取流程
                        try {
                            WxOAuth2AccessToken auth2AccessToken = wxService.getOAuth2Service().getAccessToken(code);
                            // todo 判断WechatWebAuthorization.scope
                            // todo 存入user_oauth表
                            request.getSession().setAttribute(UcConst.WX_SESSION_OPEN_ID, auth2AccessToken.getOpenId());
                            return true;
                        } catch (WxErrorException e) {
                            if (e.getError().getErrorCode() == 40163 || e.getError().getErrorCode() == 40029) {
                                // oauth_code已使用,不合法的oauth_code
                                // 重新获取code,排除code和status
                                StringBuffer url;
                                Map<String, String> params = HttpContextUtils.getParameterMap(request, "code", "state");
                                if (params.isEmpty()) {
                                    url = request.getRequestURL();
                                } else {
                                    url = request.getRequestURL().append("?");
                                    for (String key : params.keySet()) {
                                        url.append(key).append("=").append(params.get(key));
                                    }
                                }
                                String oauth2buildAuthorizationUrl = wxService.getOAuth2Service().buildAuthorizationUrl(url.toString(), annotation.scope(), "wx#wechat_redirect");
                                try {
                                    response.sendRedirect(oauth2buildAuthorizationUrl);
                                    return false;
                                } catch (IOException e2) {
                                    e.printStackTrace();
                                    throw new OnexException(ErrorCode.WX_API_ERROR, "跳转微信授权页面失败");
                                }
                            } else {
                                throw new OnexException(ErrorCode.WX_API_ERROR, e.getError().getErrorCode() + ":" + e.getError().getErrorMsg());
                            }
                        }
                    }
                } else {
                    // todo 取出请求中的code和status
                }
            }
        }
        return true;
    }

}
