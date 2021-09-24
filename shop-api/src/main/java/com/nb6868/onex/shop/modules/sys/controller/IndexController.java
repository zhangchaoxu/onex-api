package com.nb6868.onex.shop.modules.sys.controller;

import cn.hutool.core.lang.Dict;
import cn.hutool.system.SystemUtil;
import com.nb6868.onex.common.annotation.AccessControl;
import com.nb6868.onex.common.pojo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统接口
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController("Index")
@RequestMapping("/")
@Validated
@Api(tags = "首页")
@Slf4j
public class IndexController {

    @GetMapping("/")
    @AccessControl("/")
    @ApiOperation("基本信息")
    public Result<?> index() {
        Dict dict = Dict.create()
                .set("docUrl", "/doc.html")
                .set("jvm", SystemUtil.getJvmInfo())
                .set("os", SystemUtil.getOsInfo())
                .set("host", SystemUtil.getHostInfo())
                .set("totalThreadCount", SystemUtil.getTotalThreadCount());
        return new Result<>().success(dict);
    }

}
