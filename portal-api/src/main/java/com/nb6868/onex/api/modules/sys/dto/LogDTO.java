package com.nb6868.onex.api.modules.sys.dto;

import com.nb6868.onex.common.pojo.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "日志")
public class LogDTO extends BaseDTO {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "类型")
    private String type;

    @ApiModelProperty(value = "内容")
    private String content;

    @ApiModelProperty(value = "用户操作")
    private String operation;

    @ApiModelProperty(value = "请求URI")
    private String uri;

    @ApiModelProperty(value = "请求方式")
    private String method;

    @ApiModelProperty(value = "请求参数")
    private String params;

    @ApiModelProperty(value = "耗时(毫秒)")
    private Long requestTime;

    @ApiModelProperty(value = "用户代理")
    private String userAgent;

    @ApiModelProperty(value = "操作IP")
    private String ip;

    @ApiModelProperty(value = "状态  0：失败   1：成功")
    private Integer state;

    @ApiModelProperty(value = "用户名")
    private String createName;

}
