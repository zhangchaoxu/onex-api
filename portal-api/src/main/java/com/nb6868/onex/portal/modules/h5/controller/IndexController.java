package com.nb6868.onex.portal.modules.h5.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller("H5Index")
@RequestMapping("/h5/html/")
@Validated
@Api(tags = "H5")
public class IndexController {

    @GetMapping("page1")
    @ApiOperation("page1")
    // @AccessControl
    public String page1(ModelMap map) {
        map.put("title", "tt");
        map.put("message", "222");
        return "h5/msg";
    }

}
