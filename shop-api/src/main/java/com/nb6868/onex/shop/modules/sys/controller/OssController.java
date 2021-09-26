package com.nb6868.onex.shop.modules.sys.controller;

import cn.hutool.core.collection.CollUtil;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.oss.AbstractOssService;
import com.nb6868.onex.common.oss.OssPropsConfig;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.util.MultipartFileUtils;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.shop.modules.sys.entity.OssEntity;
import com.nb6868.onex.shop.modules.sys.service.OssService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@RestController("ShopSysOssController")
@RequestMapping("/sys/oss")
@Validated
@Slf4j
@Api(tags = "素材库")
@ApiSupport(order = 20)
public class OssController {

    @Autowired
    private OssService ossService;

    @PostMapping("upload")
    @ApiOperation(value = "上传单文件")
    @LogOperation("上传单文件")
    public Result<String> upload(@RequestParam("file") MultipartFile file,
                            @RequestParam(required = false, defaultValue = "OSS_PUBLIC", name = "存储配置") String paramCode,
                            @RequestParam(required = false, name = "路径前缀") String prefix) {
        AssertUtils.isTrue(file.isEmpty(), ErrorCode.UPLOAD_FILE_EMPTY);

        // 上传文件
        String url = OssPropsConfig.getService(paramCode).upload(prefix, file);
        //保存文件信息
        OssEntity oss = new OssEntity();
        oss.setUrl(url);
        oss.setFilename(file.getOriginalFilename());
        oss.setSize(file.getSize());
        oss.setContentType(file.getContentType());
        ossService.save(oss);

        return new Result<String>().success(url);
    }

    @PostMapping("uploadBase64")
    @ApiOperation(value = "上传单文件base64")
    @LogOperation("上传单文件base64")
    public Result<String> uploadBase64(@RequestParam(name = "文件base64") String fileBase64,
            @RequestParam(required = false, defaultValue = "OSS_PUBLIC", name = "配置参数") String paramCode,
            @RequestParam(required = false, name = "路径前缀") String prefix) {
        // 将base64转成file
        MultipartFile file = MultipartFileUtils.base64ToMultipartFile(fileBase64);
        AssertUtils.isTrue(file.isEmpty(), ErrorCode.UPLOAD_FILE_EMPTY);

        // 上传文件
        String url = OssPropsConfig.getService(paramCode).upload(prefix, file);
        //保存文件信息
        OssEntity oss = new OssEntity();
        oss.setUrl(url);
        oss.setFilename(file.getOriginalFilename());
        oss.setSize(file.getSize());
        oss.setContentType(file.getContentType());
        ossService.save(oss);

        return new Result<String>().success(url);
    }

    @PostMapping("uploadMulti")
    @ApiOperation(value = "上传多文件")
    @LogOperation("上传多文件")
    public Result<String> uploadMulti(@RequestParam(required = false, defaultValue = "OSS_PUBLIC", name = "配置参数") String paramCode,
                                 @RequestParam("file") @NotEmpty(message = "文件不能为空") MultipartFile[] files,
                                 @RequestParam(required = false, name = "路径前缀") String prefix) {
        List<String> srcList = new ArrayList<>();
        AbstractOssService abstractOssService = OssPropsConfig.getService(paramCode);
        for (MultipartFile file : files) {
            // 上传文件
            String url = abstractOssService.upload(prefix, file);
            //保存文件信息
            OssEntity oss = new OssEntity();
            oss.setUrl(url);
            oss.setFilename(file.getOriginalFilename());
            oss.setSize(file.getSize());
            oss.setContentType(file.getContentType());
            ossService.save(oss);
            srcList.add(url);
        }
        return new Result<String>().success(CollUtil.join(srcList, ","));
    }

}
