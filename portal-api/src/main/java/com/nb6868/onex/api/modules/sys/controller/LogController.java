package com.nb6868.onex.api.modules.sys.controller;

import com.nb6868.onex.api.modules.sys.dto.LogDTO;
import com.nb6868.onex.api.modules.sys.excel.LogExcel;
import com.nb6868.onex.api.modules.sys.service.LogService;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.pojo.PageData;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.util.ExcelUtils;
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

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sys/log")
@Validated
@Api(tags = "日志")
public class LogController {
    @Autowired
    LogService logService;

    @GetMapping("page")
    @ApiOperation("分页")
    @RequiresPermissions("sys:log:info")
    public Result<?> page(@ApiIgnore @RequestParam Map<String, Object> params) {
        PageData<LogDTO> page = logService.pageDto(params);

        return new Result<>().success(page);
    }

    @GetMapping("export")
    @ApiOperation("导出")
    @LogOperation("导出")
    @RequiresPermissions("sys:log:export")
    public void export(@ApiIgnore @RequestParam Map<String, Object> params, HttpServletResponse response) {
        List<LogDTO> list = logService.listDto(params);

        ExcelUtils.exportExcelToTarget(response, "日志", list, LogExcel.class);
    }

}
