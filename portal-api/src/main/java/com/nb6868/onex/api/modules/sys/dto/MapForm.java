package com.nb6868.onex.api.modules.sys.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nb6868.onex.common.pojo.BaseForm;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "用户")
public class MapForm extends BaseForm {

    @ApiModelProperty(value = "用户名", required = true)
    @NotBlank(message = "{username.require}", groups = DefaultGroup.class)
    private String username;

    @ApiModelProperty(value = "密码")
    @NotBlank(message = "{password.require}", groups = DefaultGroup.class)
    @JsonIgnore
    private String password;

}
