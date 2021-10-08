package com.nb6868.onex.api.config;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.json.JSONArray;
import com.nb6868.onex.api.modules.sys.entity.LogEntity;
import com.nb6868.onex.api.modules.sys.service.LogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.Serializable;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 异步任务配置
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Configuration
@ConditionalOnProperty(name = "onex.async.enable", havingValue = "true")
@EnableAsync
@Slf4j
public class AsyncConfig implements AsyncConfigurer {

    @Autowired
    private LogService logService;

    @Override
    @Bean(name = "taskExecutor")
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(16); //核心线程数
        executor.setMaxPoolSize(20);  //最大线程数
        executor.setQueueCapacity(1000); //队列大小
        executor.setKeepAliveSeconds(300); //线程最大空闲时间
        executor.setThreadNamePrefix("onex-AsyncExecutor-");
        // 拒绝策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return executor;
    }

    /**
     * 注意,只会捕捉空返回值的异步方法
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (throwable, method, objects) -> {
            // 只会捕捉到空返回值的任务
            log.error("Async Exception method={}", method.getDeclaringClass().getName() + "." + method.getName());
            JSONArray paramArray = new JSONArray();
            for (Object param : objects) {
                log.info("Async Exception param={}", param);
                if (param instanceof Serializable) {
                    paramArray.put(param.toString());
                } else {
                    paramArray.put(null);
                }
            }
            log.error("Async Exception message=", throwable);
            // 异常信息
            LogEntity logEntity = new LogEntity();
            logEntity.setType("error.async");
            logEntity.setState(0);
            logEntity.setUri(method.getDeclaringClass().getName() + "." + method.getName());
            logEntity.setContent(ExceptionUtil.stacktraceToString(throwable));
            logEntity.setParams(paramArray.size() > 0 ? paramArray.toString() : null);
            try {
                logService.save(logEntity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }
}
