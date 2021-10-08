package com.nb6868.onex.api.modules.sys.controller;

import com.nb6868.onex.common.pojo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController("SysWebSocket")
@RequestMapping("/webSocket")
@Validated
@Api(tags="WebSocket")
public class WebSocketController {

    @Autowired
    private WebSocketServer webSocketServer;

    @GetMapping("getOpenSockets")
    @RequiresPermissions("sys:websocket:info")
    @ApiOperation("获得目前连接的Socket")
    public Result<?> getOpenSockets() {
        List<String> sidList = new ArrayList<>();
        webSocketServer.getSessionPool().forEach((sid, session) -> sidList.add(sid));
        return new Result<>().success(sidList);
    }

    @PostMapping("sendOneMessage")
    @RequiresPermissions("sys:websocket:send")
    @ApiOperation("发送单点消息")
    public Result<?> sendMultiMessage(@RequestParam @ApiParam(required = true, value = "接收消息sid") String sid, @RequestParam @ApiParam(required = true, value = "消息内容") String content) {
        webSocketServer.sendOneMessage(sid, content);
        return new Result<>();
    }

    @PostMapping("sendMultiMessage")
    @RequiresPermissions("sys:websocket:send")
    @ApiOperation("发送多点消息")
    public Result<?> sendMultiMessage(@RequestParam @ApiParam(required = true, value = "接收消息sid列表") List<String> sidList, @RequestParam @ApiParam(required = true, value = "消息内容") String content) {
        webSocketServer.sendMultiMessage(sidList, content);
        return new Result<>();
    }

    @PostMapping("sendBroadcast")
    @RequiresPermissions("sys:websocket:send")
    @ApiOperation("发送广播消息")
    public Result<?> sendBroadcast(@RequestParam @ApiParam(required = true, value = "消息内容") String content) {
        webSocketServer.sendBroadcast(content);
        return new Result<>();
    }

}
