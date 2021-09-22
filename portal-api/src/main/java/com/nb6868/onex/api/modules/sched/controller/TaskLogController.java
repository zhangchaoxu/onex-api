package com.nb6868.onex.api.modules.sched.controller;

import com.nb6868.onex.api.modules.sched.dto.TaskLogDTO;
import com.nb6868.onex.api.modules.sched.service.TaskLogService;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.pojo.PageData;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.validator.AssertUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * 定时任务日志
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController
@RequestMapping("/sched/taskLog")
@Validated
@Api(tags = "定时任务日志")
public class TaskLogController {

    @Autowired
    TaskLogService taskLogService;

    @GetMapping("page")
    @ApiOperation("分页")
    @RequiresPermissions("sched:taskLog:page")
    public Result<?> page(@ApiIgnore @RequestParam Map<String, Object> params) {
        PageData<TaskLogDTO> page = taskLogService.pageDto(params);

        return new Result<>().success(page);
    }

    @GetMapping("info")
    @ApiOperation("信息")
    @RequiresPermissions("sched:task:info")
    public Result<?> info(@NotNull(message = "{id.require}") @RequestParam Long id) {
        TaskLogDTO data = taskLogService.getDtoById(id);
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);

        return new Result<>().success(data);
    }
}
