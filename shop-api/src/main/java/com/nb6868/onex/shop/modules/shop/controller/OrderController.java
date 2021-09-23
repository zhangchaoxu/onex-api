package com.nb6868.onex.shop.modules.shop.controller;

import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.nb6868.onex.common.annotation.AccessControl;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.pojo.PageData;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.util.ConvertUtils;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.shop.modules.shop.dto.OrderDTO;
import com.nb6868.onex.shop.modules.shop.entity.OrderEntity;
import com.nb6868.onex.shop.modules.shop.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.constraints.NotNull;
import java.util.Map;

@RestController("ShopOrderController")
@RequestMapping("/shop/order")
@Validated
@Slf4j
@Api(tags="订单")
@ApiSupport(order = 110)
public class OrderController {

    @Autowired
    OrderService orderService;

    @GetMapping("page")
    @AccessControl("/page")
    @ApiOperation("分页")
    public Result<?> page(@ApiIgnore @RequestParam Map<String, Object> params) {
        PageData<?> page = null;//goodsService.pageDto(params);

        return new Result<>().success(page);
    }

    @GetMapping("/info")
    @AccessControl("/info")
    @ApiOperation("信息")
    public Result<?> info(@NotNull(message = "{id.require}") @RequestParam Long id) {
        OrderEntity entity = orderService.getById(id);
        AssertUtils.isNull(entity, ErrorCode.DB_RECORD_EXISTS);
        // 转成dto
        OrderDTO dto = ConvertUtils.sourceToTarget(entity, OrderDTO.class);

        return new Result<>().success(dto);
    }

}
