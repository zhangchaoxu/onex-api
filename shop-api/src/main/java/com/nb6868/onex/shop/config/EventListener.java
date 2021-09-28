package com.nb6868.onex.shop.config;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 日志订阅
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Component
public class EventListener {

    // 同步方法
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void log(LogEvent loginEvent) {
        System.out.println("接收到登录消息++发送短信，telePhone="
                + loginEvent.getName()
                + "== " + LocalDateTime.now());
    }

}
