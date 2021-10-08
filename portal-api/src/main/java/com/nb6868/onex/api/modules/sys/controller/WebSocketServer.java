package com.nb6868.onex.api.modules.sys.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * WebSocketServer
 * see {https://mp.weixin.qq.com/s/UE_iyHZ4CWmDzx9dr6m-pQ}
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Log4j2
@Component
@ServerEndpoint("/ws/{sid}")
public class WebSocketServer {

    // 线程安全Set，存放每个客户端对应的MyWebSocket对象
    private final static CopyOnWriteArraySet<WebSocketServer> webSockets = new CopyOnWriteArraySet<>();
    private final static Map<String, Session> sessionPool = new HashMap<>();

    // 与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam(value = "sid") String sid) {
        this.session = session;
        webSockets.add(this);
        sessionPool.put(sid, session);
        log.debug("[websocket]" + sid + "接入,当前总数为:" + webSockets.size());
        // 发送反馈消息
        sendOneMessage(sid, "connect success [" + sid + "]");
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        webSockets.remove(this);
        log.debug("[websocket]" + "连接断开,当前总数为:" + webSockets.size());
    }

    /**
     * 收到客户端消息后调用的方法
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, @PathParam(value = "sid") String sid) {
        log.debug("[websocket]" + "收到客户端消息:" + message);
        // 发送反馈消息
        sendOneMessage(sid, "message received [" + sid + "]");
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误");
        error.printStackTrace();
    }

    /**
     * 发送广播消息
     * @param message 广播的消息
     */
    public void sendBroadcast(String message) {
        webSockets.forEach(webSocketServer -> {
            log.debug("[websocket]" + "广播消息:" + message);
            try {
                webSocketServer.session.getAsyncRemote().sendText(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 发送单点消息
     */
    public void sendOneMessage(String sid, String message) {
        log.debug("[websocket]" + "单点消息:" + message);
        Session session = sessionPool.get(sid);
        if (session != null) {
            try {
                session.getAsyncRemote().sendText(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 发送多点消息
     */
    public void sendMultiMessage(List<String> sidList, String message) {
        log.debug("[websocket]" + "单点消息:" + message);
        sidList.forEach(sid -> {
            Session session = sessionPool.get(sid);
            if (session != null) {
                try {
                    session.getAsyncRemote().sendText(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
