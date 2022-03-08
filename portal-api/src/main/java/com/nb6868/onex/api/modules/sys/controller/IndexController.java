package com.nb6868.onex.api.modules.sys.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Dict;
import com.nb6868.onex.common.annotation.AccessControl;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.pojo.CommonForm;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.validator.ValidatorUtils;
import com.sun.management.OperatingSystemMXBean;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/")
    @ApiOperation("首页")
    @AccessControl
    public Result<?> index() {
        return new Result<>().success("api success");
    }

    @GetMapping("/logTest/{path}")
    @ApiOperation("日志测试")
    @LogOperation("日志测试")
    @AccessControl("logTest/**")
    public Result<?> logTest(@PathVariable String path, @NotNull(message = "{pid.require}") Long id1, @NotNull(message = "{id.require}") Long id2) {
        log.info("index logTest");
        return new Result<>().success("api success");
    }

    @PostMapping("/logPostTest/{path}")
    @ApiOperation("日志测试Post")
    @LogOperation("日志测试Post")
    @AccessControl("logPostTest/**")
    public Result<?> logPostTest(@PathVariable String path, @RequestBody CommonForm form) {
        ValidatorUtils.validateEntity(form, CommonForm.ListGroup.class, CommonForm.OneGroup.class);
        log.info("index logTest");
        return new Result<>().success("api success");
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
