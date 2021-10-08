package com.nb6868.onex.api.modules.sys.controller;

import com.nb6868.onex.common.pojo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * WebSocket
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController
@RequestMapping("/webSocket")
@Validated
@Api(tags="WebSocket")
public class WebSocketController {

    @Autowired
    private WebSocketServer webSocketServer;

    @GetMapping("getOpenSockets")
    @ApiOperation("获得目前连接的Socket")
    public Result<?> getOpenSockets() {
        return new Result<>();
    }

    @PostMapping("sendOneMessage")
    @ApiOperation("发送单点消息")
    public Result<?> sendOneMessage(@RequestParam String sid, @RequestParam String content) {
        webSocketServer.sendOneMessage(sid, content);
        return new Result<>();
    }

}
