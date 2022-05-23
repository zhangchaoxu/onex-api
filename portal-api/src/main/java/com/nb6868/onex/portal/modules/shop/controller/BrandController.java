package com.nb6868.onex.portal.modules.shop.controller;

import com.nb6868.onex.portal.modules.shop.dto.BrandDTO;
import com.nb6868.onex.portal.modules.shop.excel.BrandExcel;
import com.nb6868.onex.portal.modules.shop.service.BrandService;
import com.nb6868.onex.common.annotation.DataSqlScope;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.pojo.PageData;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.util.ExcelUtils;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.common.validator.group.AddGroup;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import com.nb6868.onex.common.validator.group.UpdateGroup;
import com.nb6868.onex.portal.modules.shop.service.GoodsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * 品牌
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController
@RequestMapping("/shop/brand")
@Validated
@Api(tags = "品牌")
public class BrandController {
    @Autowired
    private BrandService brandService;
    @Autowired
    private GoodsService goodsService;

    @DataSqlScope(tableAlias = "shop_brand", tenantFilter = true)
    @GetMapping("list")
    @ApiOperation("列表")
    @RequiresPermissions("shop:brand:list")
    public Result<?> list(@ApiIgnore @RequestParam Map<String, Object> params) {
        List<BrandDTO> list = brandService.listDto(params);

        return new Result<>().success(list);
    }

    @DataSqlScope(tableAlias = "shop_brand", tenantFilter = true)
    @GetMapping("page")
    @ApiOperation("分页")
    @RequiresPermissions("shop:brand:page")
    public Result<?> page(@ApiIgnore @RequestParam Map<String, Object> params) {
        PageData<BrandDTO> page = brandService.pageDto(params);

        return new Result<>().success(page);
    }

    @GetMapping("info")
    @ApiOperation("信息")
    @RequiresPermissions("shop:brand:info")
    public Result<?> info(@NotNull(message = "{id.require}") @RequestParam Long id) {
        BrandDTO data = brandService.getDtoById(id);
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);

        return new Result<>().success(data);
    }

    @PostMapping("save")
    @ApiOperation("保存")
    @LogOperation("保存")
    @RequiresPermissions("shop:brand:save")
    public Result<?> save(@Validated(value = {DefaultGroup.class, AddGroup.class}) @RequestBody BrandDTO dto) {
        brandService.saveDto(dto);

        return new Result<>().success(dto);
    }

    @PutMapping("update")
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions("shop:brand:update")
    public Result<?> update(@Validated(value = {DefaultGroup.class, UpdateGroup.class}) @RequestBody BrandDTO dto) {
        brandService.updateDto(dto);

        return new Result<>().success(dto);
    }

    @DeleteMapping("delete")
    @ApiOperation("删除")
    @LogOperation("删除")
    @RequiresPermissions("shop:brand:delete")
    public Result<?> delete(@NotNull(message = "{id.require}") @RequestParam Long id) {
        AssertUtils.isTrue(goodsService.query().eq("brand_id", id).exists(), "存在商品,不允许删除");
        brandService.logicDeleteById(id);

        return new Result<>();
    }

    @GetMapping("export")
    @ApiOperation("导出")
    @LogOperation("导出")
    @RequiresPermissions("shop:brand:export")
    public void export(@ApiIgnore @RequestParam Map<String, Object> params, HttpServletResponse response) {
        List<BrandDTO> list = brandService.listDto(params);

        ExcelUtils.exportExcelToTarget(response, "品牌", list, BrandExcel.class);
    }

}
