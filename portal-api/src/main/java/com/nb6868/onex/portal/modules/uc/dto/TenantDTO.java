package com.nb6868.onex.portal.modules.uc.dto;

import com.nb6868.onex.common.pojo.BaseDTO;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * 租户
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "租户")
public class TenantDTO extends BaseDTO {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "编码")
	@NotBlank(message = "编码不能为空", groups = DefaultGroup.class)
	private String code;

	@ApiModelProperty(value = "名称")
	private String name;

	@ApiModelProperty(value = "有效期开始")
	private Date validStartTime;

	@ApiModelProperty(value = "有效期结束")
	private Date validEndTime;

	@ApiModelProperty(value = "状态 0 无效 1 有效")
	private Integer state;

}
