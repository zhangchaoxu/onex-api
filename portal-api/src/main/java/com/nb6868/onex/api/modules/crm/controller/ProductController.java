package com.nb6868.onex.api.modules.crm.controller;

import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.api.modules.crm.dto.ProductDTO;
import com.nb6868.onex.api.modules.crm.dto.ProductQueryFrom;
import com.nb6868.onex.api.modules.crm.entity.ProductCategoryEntity;
import com.nb6868.onex.api.modules.crm.entity.ProductEntity;
import com.nb6868.onex.api.modules.crm.excel.ProductExcel;
import com.nb6868.onex.api.modules.crm.service.ProductCategoryService;
import com.nb6868.onex.api.modules.crm.service.ProductService;
import com.nb6868.onex.api.shiro.SecurityUser;
import com.nb6868.onex.common.annotation.AccessControl;
import com.nb6868.onex.common.annotation.DataSqlScope;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.jpa.QueryHelpPlus;
import com.nb6868.onex.common.pojo.Const;
import com.nb6868.onex.common.pojo.MsgResult;
import com.nb6868.onex.common.pojo.PageData;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.util.ConvertUtils;
import com.nb6868.onex.common.util.ExcelUtils;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.common.validator.ValidatorUtils;
import com.nb6868.onex.common.validator.group.AddGroup;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import com.nb6868.onex.common.validator.group.UpdateGroup;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * CRM产品
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController
@RequestMapping("/crm/product/")
@Validated
@Api(tags = "CRM产品")
public class ProductController {
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductCategoryService productCategoryService;

    @PostMapping("list")
    @ApiOperation("列表")
    // @RequiresPermissions("crm:product:list")
    @AccessControl("list")
    public Result<?> list(@RequestBody ProductQueryFrom from) {
        QueryWrapper<ProductEntity> queryWrapper = QueryHelpPlus.getPredicate(ProductEntity.class, from);
        List<ProductDTO> list = productService.listDto(queryWrapper);

        return new Result<>().success(list);
    }

    @DataSqlScope(tableAlias = "crm_product", tenantFilter = true)
    @GetMapping("page")
    @ApiOperation("分页")
    @RequiresPermissions("crm:product:page")
    public Result<?> page(@ApiIgnore @RequestParam Map<String, Object> params) {
        PageData<ProductDTO> page = productService.pageDto(params);

        return new Result<>().success(page);
    }

    @GetMapping("info")
    @ApiOperation("信息")
    @RequiresPermissions("crm:product:info")
    public Result<?> info(@RequestParam @NotNull(message = "{id.require}") Long id) {
        ProductDTO data = productService.getDtoById(id);

        return new Result<>().success(data);
    }

    @PostMapping("save")
    @ApiOperation("保存")
    @LogOperation("保存")
    @RequiresPermissions("crm:product:save")
    public Result<?> save(@Validated(value = {DefaultGroup.class, AddGroup.class}) @RequestBody ProductDTO dto) {
        productService.saveDto(dto);

        return new Result<>().success(dto);
    }

    @PutMapping("update")
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions("crm:product:update")
    public Result<?> update(@Validated(value = {DefaultGroup.class, UpdateGroup.class}) @RequestBody ProductDTO dto) {
        productService.updateDto(dto);

        return new Result<>().success(dto);
    }

    @DeleteMapping("delete")
    @ApiOperation("删除")
    @LogOperation("删除")
    @RequiresPermissions("crm:product:delete")
    public Result<?> delete(@NotNull(message = "{id.require}") @RequestParam Long id) {
        productService.logicDeleteById(id);

        return new Result<>();
    }

    @DeleteMapping("deleteBatch")
    @ApiOperation("批量删除")
    @LogOperation("批量删除")
    @RequiresPermissions("crm:product:deleteBatch")
    public Result<?> deleteBatch(@NotEmpty(message = "{ids.require}")@RequestBody List<Long> ids) {
        productService.logicDeleteByIds(ids);

        return new Result<>();
    }

    @DataSqlScope(tableAlias = "crm_product", tenantFilter = true)
    @GetMapping("export")
    @ApiOperation("导出")
    @LogOperation("导出")
    @RequiresPermissions("crm:product:export")
    public void export(@ApiIgnore @RequestParam Map<String, Object> params, HttpServletResponse response) {
        List<ProductDTO> list = productService.listDto(params);

        ExcelUtils.exportExcelToTarget(response, "产品", list, ProductExcel.class);
    }

    @PostMapping("import")
    @ApiOperation("导入")
    @LogOperation("导入")
    @RequiresPermissions("crm:product:import")
    public Result<?> importExcel(@RequestParam("file") MultipartFile file) {
        AssertUtils.isTrue(file.isEmpty(), ErrorCode.UPLOAD_FILE_EMPTY);

        ImportParams params = new ImportParams();
        params.setStartSheetIndex(0);
        List<ProductExcel> list = ExcelUtils.importExcel(file, ProductExcel.class, params);
        AssertUtils.isTrue(list.isEmpty(), ErrorCode.ERROR_REQUEST, "Excel内容为空");
        AssertUtils.isTrue(list.size() > Const.EXCEL_IMPORT_LIMIT, ErrorCode.ERROR_REQUEST, "单次导入不得超过" + Const.EXCEL_IMPORT_LIMIT + "条");

        List<MsgResult> result = new ArrayList<>();
        for (ProductExcel item : list) {
            ProductDTO dto = ConvertUtils.sourceToTarget(item, ProductDTO.class);
            ProductCategoryEntity productCategory = productCategoryService.getByName(dto.getCategoryName(), SecurityUser.getUser().getTenantCode());
            if (null == productCategory) {
                result.add(new MsgResult().error(ErrorCode.ERROR_REQUEST, "未找到产品分类:" + dto.getCategoryName()));
            } else {
                dto.setCategoryId(productCategory.getId());
                dto.setCategoryName(productCategory.getName());
                MsgResult validateResult = ValidatorUtils.getValidateResult(dto, DefaultGroup.class, AddGroup.class);
                if (validateResult.isSuccess()) {
                    try {
                        productService.saveDto(dto);
                        result.add(new MsgResult().success("导入成功"));
                    } catch (Exception e) {
                        result.add(new MsgResult().error(ErrorCode.ERROR_REQUEST, e.getMessage()));
                    }
                } else {
                    result.add(validateResult);
                }
            }

        }
        return new Result<>().success(result);
    }

}

