package com.nb6868.onex.api.modules.sys.task;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.thread.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.Future;

/**
 * 系统相关异步任务
 */
@Component
@Slf4j
@Async
public class SysAsyncTask {

    /**
     * 长耗时任务
     */
    public void longTimeTask(int count) {
        do {
            ThreadUtil.sleep(2000);
            log.error("test long time async task, {}", count);
            count--;
        } while (count > 0);
        log.error("long time task finish");
    }

    /**
     * 测试异常捕捉
     */
    public Future<Dict> throwException() {
        log.info("throwException");
        throw new IllegalArgumentException("throwException");
    }

    /**
     * 测试异常捕捉
     */
    public void throwExceptionNoReturn(String params) {
        log.info("throwExceptionNoReturn");
        throw new IllegalArgumentException("throwExceptionNoReturn");
    }

}
