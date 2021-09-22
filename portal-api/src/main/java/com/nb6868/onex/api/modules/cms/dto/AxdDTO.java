package com.nb6868.onex.api.modules.cms.dto;

import com.nb6868.onex.common.pojo.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 广告位
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "广告位")
public class AxdDTO extends BaseDTO {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "标题")
	private String name;

	@ApiModelProperty(value = "位置")
	private String position;

	@ApiModelProperty(value = "链接")
	private String link;

	@ApiModelProperty(value = "备注")
	private String remark;

	@ApiModelProperty(value = "图片")
	private String imgs;

	@ApiModelProperty(value = "排序")
	private Integer sort;

	@ApiModelProperty(value = "是否需要登录 0 不需要 1需要")
	private Integer needLogin;

	@ApiModelProperty(value = "租户id")
	private Long tenantId;

	@ApiModelProperty(value = "租户名称")
	private String tenantName;

}
