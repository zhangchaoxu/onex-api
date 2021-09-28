package com.nb6868.onex.shop.config;

import org.greenrobot.eventbus.EventBus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
public class EventHandler {

    @Autowired
    private EventBus eventBus;
    @Autowired
    private EventListener orderListener;

    /**
     * 注册总线
     */
    @PostConstruct
    public void init() {
        eventBus.register(orderListener);
    }

    /**
     * 注销总线
     */
    @PreDestroy
    public void destroy() {
        eventBus.unregister(orderListener);
    }

    /**
     * 发布活动
     * @param obj 事件
     */
    public void eventPost(Object obj){
        eventBus.post(obj);
    }

}
