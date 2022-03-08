package com.nb6868.onex.api.handler;

import cn.hutool.core.exceptions.ExceptionUtil;
import com.nb6868.onex.api.modules.sys.entity.LogEntity;
import com.nb6868.onex.api.modules.sys.service.LogService;
import com.nb6868.onex.common.exception.BaseExceptionHandler;
import com.nb6868.onex.common.util.HttpContextUtils;
import com.nb6868.onex.common.util.JacksonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 异常处理器
 *
 * @author Charles (zhancgchaoxu@gmail.com)
 */
@RestControllerAdvice
@Slf4j
public class OnexExceptionHandler extends BaseExceptionHandler {

    @Autowired
    private LogService logService;

    /**
     * 保存异常日志
     */
    @Override
    protected void saveLog(HttpServletRequest request, Exception ex) {
        LogEntity logEntity = new LogEntity();
        logEntity.setType("error");
        logEntity.setState(0);
        // 请求相关信息
        if (request == null) {
            request = HttpContextUtils.getHttpServletRequest();
        }
        if (null != request) {
            logEntity.setIp(HttpContextUtils.getIpAddr(request));
            logEntity.setUserAgent(request.getHeader(HttpHeaders.USER_AGENT));
            logEntity.setUri(request.getRequestURI());
            logEntity.setMethod(request.getMethod());
            Map<String, String> params = HttpContextUtils.getParameterMap(request);
            if (!CollectionUtils.isEmpty(params)) {
                logEntity.setParams(JacksonUtils.pojoToJson(params));
            }
        }
        // 异常信息
        logEntity.setContent(ExceptionUtil.stacktraceToString(ex));
        // 保存
        try {
            logService.save(logEntity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
