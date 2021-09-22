package com.nb6868.onex.shop.modules.shop.dto;

import com.nb6868.onex.common.validator.group.AddGroup;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import com.nb6868.onex.common.validator.group.UpdateGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 购物车
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "购物车")
public class CartDTO implements Serializable {

	@ApiModelProperty(value = "id")
	@Null(message = "{id.null}", groups = AddGroup.class)
	@NotNull(message = "{id.require}", groups = UpdateGroup.class)
	private Long id;

	@ApiModelProperty(value = "商品id", required = true)
	@NotNull(message = "商品id不能为空", groups = DefaultGroup.class)
	private Long goodsId;

	@ApiModelProperty(value = "数量", required = true)
	@NotNull(message = "商品数量不能为空", groups = DefaultGroup.class)
	private BigDecimal qty;

	@ApiModelProperty(value = "状态0 未下单 1 已下单")
	private Integer state;

	@ApiModelProperty(value = "勾选状态0 未勾选 1 已勾选")
	private Integer checked;

}
