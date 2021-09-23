package com.nb6868.onex.shop.modules.shop.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nb6868.onex.common.pojo.BaseTenantEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("shop_order")
@Alias("shop_order")
public class OrderEntity extends BaseTenantEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 状态 0：待付款  1：待发货  2:待收货  3：待评价  -1：退款   -2：售后
     */
    private Integer state;
    /**
     * 支付状态 0 待支付 1已支付 -1已退款
     */
    private Integer payState;
    /**
     * 订单号
     */
    private String no;
    /**
     * 购买方式
     */
    private String buyType;
    /**
     * 买方用户id
     */
    private Long userId;
    /**
     * 卖方用户id
     */
    private Long sellUserId;
    /**
     * 用户备注
     */
    private String userRemark;
    /**
     * 商品详情,简略商品信息
     */
    private String goodsDetail;
    /**
     * 商品费用
     */
    private BigDecimal goodsPrice;
    /**
     * 物流费用
     */
    private BigDecimal expressPrice;
    /**
     * 商品折扣费用
     */
    private BigDecimal goodsDiscountPrice;
    /**
     * 订单总费用
     */
    private BigDecimal totalPrice;
    /**
     * 积分抵扣
     */
    private Integer pointsDeduct;
    /**
     * 积分抵扣金额
     */
    private BigDecimal pointPrice;
    /**
     * 支付费用
     */
    private BigDecimal payPrice;
    /**
     * 支付类型 0 无须支付 1 现金交易 2 银行转账 3 支付宝支付 4 微信支付 5 账户余额支付
     */
    private Integer payType;
    /**
     * 下单时间
     */
    private Date orderTime;
    /**
     * 支付交易ID
     */
    private String payTransactionId;
    /**
     * 付款时间
     */
    private Date payTime;
    /**
     * 发货时间
     */
    private Date deliverTime;
    /**
     * 物流单号
     */
    private String expressNo;
    /**
     * 物流类型
     */
    private String expressType;
    /**
     * 收货地址id
     */
    private Long receiverId;
    /**
     * 收件人
     */
    private String receiverConsignee;
    /**
     * 收件人电话
     */
    private String receiverMobile;
    /**
     * 收件详细地址
     */
    private String receiverAddress;
    /**
     * 收件地址区域编码
     */
    private String receiverRegionCode;
    /**
     * 收件地址区域
     */
    private String receiverRegionName;
    /**
     * 收件地址纬度
     */
    private Double receiverRegionLat;
    /**
     * 收件地址经度
     */
    private String receiverRegionLng;

}
