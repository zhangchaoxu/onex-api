package com.nb6868.onex.api.modules.sys.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Dict;
import com.nb6868.onex.api.modules.sys.dto.TestForm;
import com.nb6868.onex.common.annotation.AccessControl;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.validator.ValidatorUtils;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import com.sun.management.OperatingSystemMXBean;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import java.lang.management.ManagementFactory;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 系统接口
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController
@RequestMapping("/")
@Validated
@Api(tags = "首页")
@Slf4j
public class IndexController {

    @Autowired
    private Environment env;

    @GetMapping("/")
    @ApiOperation("index")
    @AccessControl("/")
    public Result<?> index() {
        Dict result = Dict.create()
                .set("onex", Dict.create()
                        .set("parent-artifact-id", env.getProperty("onex.parent-artifact-id"))
                        .set("artifact-id", env.getProperty("onex.artifact-id"))
                        .set("version", env.getProperty("onex.version"))
                        .set("build-time", env.getProperty("onex.build-time")))
                .set("app", Dict.create()
                        .set("parent-artifact-id", env.getProperty("onex.app.parent-artifact-id"))
                        .set("artifact-id", env.getProperty("onex.app.artifact-id"))
                        .set("version", env.getProperty("onex.app.version"))
                        .set("build-time", env.getProperty("onex.app.build-time")));
        return new Result<>().success(result);
    }

    @GetMapping("/logGetTest")
    @ApiOperation("日志测试Get")
    @LogOperation("日志测试Get")
    @AccessControl("logGetTest")
    public Result<?> logTest(@ApiParam(value = "xx的id", required = false) @Max(value = 10, message = "不允许超过10") @RequestParam Long id1,
                             @ApiParam(value = "xx的id2", required = true) @NotNull(message = "{pid.require}") @RequestParam(required = false) Long id2) {
        // todo 业务逻辑
        return new Result<>();
    }

    @GetMapping("/logMapTest")
    @ApiOperation("日志测试")
    @LogOperation("日志测试")
    @AccessControl("logMapTest")
    public Result<?> logMapTest(@ApiIgnore TestForm form) {
        ValidatorUtils.validateEntity(form, DefaultGroup.class);

        return new Result<>().success("api success");
    }

    @PostMapping("/logPostTest")
    @ApiOperation("日志测试Post")
    @LogOperation("日志测试Post")
    @AccessControl("logPostTest")
    public Result<?> logPostTest(@RequestBody @Validated TestForm form) {
        ValidatorUtils.validateEntity(form);
        // todo 业务逻辑
        return new Result<>();
    }

    @GetMapping("sys/info")
    @ApiOperation("系统信息")
    public Result<?> sysInfo() {
        OperatingSystemMXBean osmx = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

        Dict data = Dict.create();
        data.set("sysTime", DateUtil.now());
        data.set("osName", System.getProperty("os.name"));
        data.set("osArch", System.getProperty("os.arch"));
        data.set("osVersion", System.getProperty("os.version"));
        data.set("userLanguage", System.getProperty("user.language"));
        data.set("userDir", System.getProperty("user.dir"));
        data.set("totalPhysical", osmx.getTotalPhysicalMemorySize() / 1024 / 1024);
        data.set("freePhysical", osmx.getFreePhysicalMemorySize() / 1024 / 1024);

        data.set("memoryRate", BigDecimal.valueOf((1 - osmx.getFreePhysicalMemorySize() * 1.0 / osmx.getTotalPhysicalMemorySize()) * 100).setScale(2, RoundingMode.HALF_UP));
        data.set("processors", osmx.getAvailableProcessors());
        data.set("jvmName", System.getProperty("java.vm.name"));
        data.set("javaVersion", System.getProperty("java.version"));
        data.set("javaHome", System.getProperty("java.home"));
        data.set("javaTotalMemory", Runtime.getRuntime().totalMemory() / 1024 / 1024);
        data.set("javaFreeMemory", Runtime.getRuntime().freeMemory() / 1024 / 1024);
        data.set("javaMaxMemory", Runtime.getRuntime().maxMemory() / 1024 / 1024);
        data.set("userName", System.getProperty("user.name"));
        data.set("systemCpuLoad", BigDecimal.valueOf(osmx.getSystemCpuLoad() * 100).setScale(2, RoundingMode.HALF_UP));
        data.set("userTimezone", System.getProperty("user.timezone"));

        return new Result<>().success(data);
    }

}
