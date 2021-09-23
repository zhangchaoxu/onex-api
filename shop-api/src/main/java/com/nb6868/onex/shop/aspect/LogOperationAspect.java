package com.nb6868.onex.shop.aspect;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.IdUtil;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.pojo.Const;
import com.nb6868.onex.common.util.HttpContextUtils;
import com.nb6868.onex.common.util.JacksonUtils;
import com.nb6868.onex.shop.modules.common.dao.LogDao;
import com.nb6868.onex.shop.shiro.SecurityUser;
import com.nb6868.onex.shop.shiro.UserDetail;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 操作日志，切面处理类
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Aspect
@Component
public class LogOperationAspect {

    @Autowired
    private LogDao logDao;

    @Pointcut("@annotation(com.nb6868.onex.common.annotation.LogOperation)")
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 记录开始执行时间
        TimeInterval timer = DateUtil.timer();
        // 需要先把param拿出来,不然processed以后可能会被修改赋值
        String requestParam = getRequestParam(joinPoint);
        try {
            // 执行方法
            Object result = joinPoint.proceed();
            // 保存日志
            saveLog(joinPoint, requestParam, timer.interval(), Const.ResultEnum.SUCCESS.value());
            return result;
        } catch (Exception e) {
            //保存日志
            saveLog(joinPoint, requestParam, timer.interval(), Const.ResultEnum.FAIL.value());
            throw e;
        }
    }

    private void saveLog(ProceedingJoinPoint joinPoint, String requestParam, long time, Integer state) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Map<String, Object> logEntity = new HashMap<>();

        try {
            Method method = joinPoint.getTarget().getClass().getDeclaredMethod(signature.getName(), signature.getParameterTypes());
            LogOperation annotation = method.getAnnotation(LogOperation.class);
            if (annotation != null) {
                // 注解上的描述
                logEntity.put("operation", annotation.value());
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        // 登录用户信息
        //
        Date now = new Date();
        UserDetail user = SecurityUser.getUser();
        logEntity.put("create_name", user.getUsername());
        logEntity.put("create_id", user.getId());
        logEntity.put("create_time", now);
        logEntity.put("update_time", now);
        logEntity.put("update_id", user.getId());
        logEntity.put("state", state);
        logEntity.put("request_time", time);

        // 请求相关信息
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        if (null != request) {
            logEntity.put("ip", HttpContextUtils.getIpAddr(request));
            logEntity.put("user_agent", request.getHeader(HttpHeaders.USER_AGENT));
            logEntity.put("uri", request.getRequestURI());
            logEntity.put("method", request.getMethod());
        }
        logEntity.put("params", requestParam);
        logEntity.put("id", IdUtil.getSnowflake().nextId());
        logEntity.put("type", "operation");
        logEntity.put("content", null);
        logDao.saveLog(logEntity);
    }

    /**
     * 从joinPoint获取参数
     */
    private String getRequestParam(ProceedingJoinPoint joinPoint) {
        // 请求参数,接口方法中的参数,可能会有HttpServletRequest、HttpServletResponse、ModelMap
        Object[] args = joinPoint.getArgs();
        List<Object> actualParam = new ArrayList<>();
        for (Object arg : args) {
            // 只处理能处理的
            if (arg == null || arg instanceof HttpServletRequest || arg instanceof HttpServletResponse || arg instanceof ModelMap) {
                // 不处理的特例
                break;
            } else if (arg instanceof MultipartFile) {
                actualParam.add(Dict.create().set("type", "file").set("name", ((MultipartFile) arg).getOriginalFilename()));
            } else if (arg instanceof MultipartFile[]) {
                MultipartFile[] files = (MultipartFile[]) arg;
                List<Dict> list = new ArrayList<>();
                for (MultipartFile file : files) {
                    list.add(Dict.create().set("type", "file").set("name", file.getOriginalFilename()));
                }
                actualParam.add(list);
            } else if (arg instanceof Serializable || arg instanceof Map) {
                actualParam.add(arg);
            } else {
                actualParam.add(Dict.create().set("type", arg.getClass().getName()));
            }
        }
        if (actualParam.size() == 1) {
            if (actualParam.get(0) instanceof String || actualParam.get(0) instanceof Long || actualParam.get(0) instanceof Integer) {
                return actualParam.get(0).toString();
            } else {
                return JacksonUtils.pojoToJson(actualParam.get(0), null);
            }
        } else if (actualParam.size() > 1) {
            return JacksonUtils.pojoToJson(actualParam, null);
        } else {
            return null;
        }
    }

}
