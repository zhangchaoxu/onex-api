package com.nb6868.onex.portal.modules.sys.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nb6868.onex.common.pojo.BaseForm;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "基础发送请求")
public class TestForm extends BaseForm {

    @ApiModelProperty(value = "ids")
    @NotEmpty(message = "{ids.require}")
    private List<Long> ids;

    @ApiModelProperty(value = "id")
    @NotNull(message = "{id.require}", groups = DefaultGroup.class)
    @NotNull(message = "{id.require}")
    private Long id;

    @ApiModelProperty(value = "备注")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String remark;

}
