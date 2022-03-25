package com.nb6868.onex.portal;

import com.nb6868.onex.portal.modules.sys.task.SysAsyncTask;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("异步任务")
public class AsyncTaskTest {

    @Autowired
    SysAsyncTask asyncTask;

    @Test
    @DisplayName("长耗时任务")
    void longTimeTask() {
        asyncTask.longTimeTask(1000);
    }

    @Test
    @DisplayName("异常捕捉无返回值")
    void throwExceptionNoReturn() {
        asyncTask.throwExceptionNoReturn("test throwExceptionNoReturn param");
    }

    @Test
    @DisplayName("异常捕捉")
    void throwException() {
        asyncTask.throwException();
    }

}
