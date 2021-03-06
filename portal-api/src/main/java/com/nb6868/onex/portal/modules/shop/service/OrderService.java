package com.nb6868.onex.portal.modules.shop.service;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.binarywang.wxpay.bean.request.WxPayRefundRequest;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.exception.OnexException;
import com.nb6868.onex.common.jpa.DtoService;
import com.nb6868.onex.common.pojo.ChangeStateForm;
import com.nb6868.onex.common.pojo.Const;
import com.nb6868.onex.common.shiro.ShiroUtils;
import com.nb6868.onex.common.util.ConvertUtils;
import com.nb6868.onex.common.util.HttpContextUtils;
import com.nb6868.onex.common.util.WrapperUtils;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.portal.modules.pay.PayConst;
import com.nb6868.onex.portal.modules.pay.dto.PayRequest;
import com.nb6868.onex.portal.modules.pay.entity.ChannelEntity;
import com.nb6868.onex.portal.modules.pay.service.ChannelService;
import com.nb6868.onex.portal.modules.pay.util.PayUtils;
import com.nb6868.onex.portal.modules.shop.ShopConst;
import com.nb6868.onex.portal.modules.shop.dao.OrderDao;
import com.nb6868.onex.portal.modules.shop.dto.OrderDTO;
import com.nb6868.onex.portal.modules.shop.dto.OrderOneClickRequest;
import com.nb6868.onex.portal.modules.shop.entity.GoodsEntity;
import com.nb6868.onex.portal.modules.shop.entity.OrderEntity;
import com.nb6868.onex.portal.modules.shop.entity.OrderItemEntity;
import com.nb6868.onex.portal.modules.shop.entity.OrderLogEntity;
import com.nb6868.onex.uc.service.ParamsService;
import com.nb6868.onex.uc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

/**
 * ??????
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class OrderService extends DtoService<OrderDao, OrderEntity, OrderDTO> {

    @Autowired
    OrderLogService orderLogService;
    @Autowired
    OrderItemService orderItemService;
    @Autowired
    GoodsService goodsService;
    @Autowired
    ParamsService paramsService;
    @Autowired
    com.nb6868.onex.portal.modules.pay.service.OrderService payOrderService;
    @Autowired
    ChannelService channelService;
    @Autowired
    UserService userService;

    @Override
    public QueryWrapper<OrderEntity> getWrapper(String method, Map<String, Object> params) {
        return new WrapperUtils<OrderEntity>(new QueryWrapper<>(), params)
                .like("no", "no")
                .eq("userId", "user_id")
                .eq("state", "state")
                // ??????????????????
                .ge("startCreateTime", "create_time")
                .le("endCreateTime", "create_time")
                .eq("tenantId", "tenant_id")
                .and("search", queryWrapper -> {
                    String search = (String) params.get("search");
                    queryWrapper.like("no", search);
                })
                .and("receiverSearch", queryWrapper -> {
                    String search = (String) params.get("receiverSearch");
                    queryWrapper.like("receiver_consignee", search)
                            .or().like("receiver_mobile", search)
                            .or().like("receiver_address", search)
                            .or().like("receiver_region_name", search);
                })
                .apply(Const.SQL_FILTER)
                .getQueryWrapper();
    }

    /**
     * ???????????????
     *
     * @param policy ????????????
     * @param prefix ????????????
     */
    public String generateOrderNo(String policy, String prefix) {
        String orderNo;
        if ("DATE_RANDOM".equalsIgnoreCase(policy)) {
            // ????????????????????????+??????+?????????
            String time = DateUtil.format(DateUtil.date(), DatePattern.PURE_DATE_PATTERN);
            orderNo = prefix + time + RandomUtil.randomNumbers(6);
        } else if ("DATETIME_RANDOM".equalsIgnoreCase(policy)) {
            // ????????????????????????+????????????+?????????
            String time = DateUtil.format(DateUtil.date(), DatePattern.PURE_DATETIME_PATTERN);
            orderNo = prefix + time + RandomUtil.randomNumbers(6);
        } else {
            throw new OnexException("?????????????????????????????????");
        }

        // ?????????????????????????????????
        if (hasDuplicated(null, "no", orderNo)) {
            return generateOrderNo(policy, prefix);
        } else {
            return orderNo;
        }
    }

    /**
     * ????????????
     *
     * @param remark ??????
     */
    public boolean cancel(Long id, String remark) {
        // ?????????????????????
        OrderEntity order = getById(id);
        AssertUtils.isNull(order, ErrorCode.DB_RECORD_NOT_EXISTED);
        AssertUtils.isTrue(order.getState() == ShopConst.OrderStateEnum.CANCELED.value(), "???????????????");

        boolean ret = update().eq("id", order.getId())
                .set("state", ShopConst.OrderStateEnum.CANCELED.value())
                .update(new OrderEntity());
        if (ret) {
            // ?????????????????????
            if (order.getPointsDeduct() > 0) {

            }
            // ?????????????????????
            // ????????????
            OrderLogEntity orderLog = new OrderLogEntity();
            orderLog.setOrderId(order.getId());
            orderLog.setType(ShopConst.OrderStateEnum.CANCELED.value());
            orderLog.setContent(remark);
            orderLogService.save(orderLog);
            return true;
        } else {
            // ??????????????????????????????,???????????????????????????????????????
            return false;
        }
    }

    /**
     * ???????????????????????????
     *
     * @param second ????????????
     */
    public boolean cancelUnPaidOrder(long second) {
        // ???????????????update???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        List<OrderEntity> orders = query().eq("pay_state", PayConst.PayTypeEnum.NO_PAY.value())
                .apply("order_time < DATE_SUB(NOW(), interval " + second + " second)")
                .list();
        for (OrderEntity order : orders) {
            // ??????????????????
            boolean ret = update().eq("id", order.getId())
                    .set("state", ShopConst.OrderStateEnum.CANCELED.value())
                    .update(new OrderEntity());
            if (ret) {
                // ?????????????????????

                // ?????????????????????
                // ????????????
                OrderLogEntity orderLog = new OrderLogEntity();
                orderLog.setOrderId(order.getId());
                orderLog.setType(ShopConst.OrderStateEnum.CANCELED.value());
                orderLog.setContent("???????????????????????????????????????");
                orderLogService.save(orderLog);
            } else {
                // ??????????????????????????????,???????????????????????????????????????
            }
        }
        return false;
    }


    public boolean checkOrder(Long orderId) {
        update().eq("id", orderId).set("state", ShopConst.OrderStateEnum.CANCELED.value()).update(new OrderEntity());
        return false;
    }

    /**
     * ????????????
     */
    public OrderEntity oneClick(OrderOneClickRequest request) {
        // ???????????????????????????
        GoodsEntity goods = goodsService.getById(request.getGoodsId());
        AssertUtils.isNull(goods, "???????????????");
        AssertUtils.isTrue(goods.getMarketable() != 1, "?????????????????????");
        AssertUtils.isTrue(request.getQty().compareTo(goods.getStock()) > 0, "??????????????????");
        AssertUtils.isTrue(goods.getQtyMin().compareTo(new BigDecimal(-1)) != 0 && request.getQty().compareTo(goods.getQtyMin()) < 0, "????????????????????????:" + goods.getQtyMin());
        AssertUtils.isTrue(goods.getQtyMax().compareTo(new BigDecimal(-1)) != 0 && request.getQty().compareTo(goods.getQtyMax()) > 0, "????????????????????????:" + goods.getQtyMax());
        // todo ????????????????????????

        // ??????order
        OrderEntity order = ConvertUtils.sourceToTarget(request, OrderEntity.class);
        order.setNo(generateOrderNo("DATE_RANDOM", ""));
        order.setState(ShopConst.OrderStateEnum.PLACED.value());
        order.setPayState(ShopConst.OrderPayStateEnum.NO_PAY.value());
        order.setBuyType("oneclick");
        order.setGoodsPrice(goods.getSalePrice().multiply(request.getQty()));
        order.setTotalPrice(goods.getSalePrice());
        order.setPayPrice(goods.getSalePrice());
        order.setExpressPrice(new BigDecimal(0));
        order.setPayType(request.getPayType());
        order.setOrderTime(DateUtil.date());
        order.setGoodsDetail(goods.getName() + request.getQty());
        order.setUserId(ShiroUtils.getUserId());
        /*order.setTenantId(goods.getTenantId());
        order.setTenantName(goods.getTenantName());*/
        order.setGoodsDiscountPrice(new BigDecimal("0"));
        save(order);

        // ??????orderItem
        OrderItemEntity orderItem = new OrderItemEntity();
        orderItem.setGoodsQty(request.getQty());
        orderItem.setGoodsId(goods.getId());
        orderItem.setGoodsName(goods.getName());
        orderItem.setGoodsCover(goods.getImgs());
        orderItem.setGoodsPrice(goods.getSalePrice());
        orderItem.setGoodTotalPrice(goods.getSalePrice().multiply(request.getQty()));
        orderItem.setOrderId(order.getId());
        /*orderItem.setTenantId(goods.getTenantId());
        orderItem.setTenantName(goods.getTenantName());*/
        orderItem.setGoodsDiscountPrice(new BigDecimal("0"));
        orderItem.setGoodsTotalDiscountPrice(new BigDecimal("0"));
        orderItemService.save(orderItem);

        // ??????????????????
        OrderLogEntity orderLog = new OrderLogEntity();
        orderLog.setOrderId(order.getId());
        orderLog.setOrderNo(order.getNo());
        orderLog.setType(1);
        orderLog.setContent("????????????");
        /*orderLog.setTenantId(goods.getTenantId());
        orderLog.setTenantName(goods.getTenantName());*/
        orderLogService.save(orderLog);

        return order;
    }

    /**
     * ????????????,??????????????????
     */
    public Serializable pay(PayRequest payRequest) {
        // ?????????????????????
        OrderEntity order = getById(payRequest.getOrderId());
        AssertUtils.isNull(order, ErrorCode.DB_RECORD_NOT_EXISTED);
        AssertUtils.isFalse(order.getState() == ShopConst.OrderStateEnum.PLACED.value(), "???????????????????????????");
        AssertUtils.isFalse(order.getPayState() == ShopConst.OrderPayStateEnum.NO_PAY.value(), "?????????????????????????????????");

        // ????????????
        ChannelEntity channel = null;//channelService.getByTenantIdAndPayType(order.getTenantId(), payRequest.getPayType());
        AssertUtils.isNull(channel, "????????????????????????");

        // ??????PayOrder
        // ??????payorder????????????
        com.nb6868.onex.portal.modules.pay.entity.OrderEntity payOrder = payOrderService.getByOrderId("shop_order", payRequest.getOrderId(), order.getPayType());
        if (null == payOrder) {
            payOrder = new com.nb6868.onex.portal.modules.pay.entity.OrderEntity();
            payOrder.setChannelId(channel.getId());
            payOrder.setChannelParam(channel.getParam());
            payOrder.setOrderId(order.getId());
            payOrder.setOrderNo(order.getNo());
            payOrder.setOrderTable("shop_order");
            payOrder.setNotifyUrl(channel.getNotifyUrl());
           /* payOrder.setTenantId(order.getTenantId());
            payOrder.setTenantName(order.getTenantName());*/
            payOrder.setState(0);
            payOrderService.save(payOrder);
        }

        // ??????????????????
        if (payRequest.getPayType().toUpperCase().startsWith("WECHAT")) {
            // ????????????,??????????????????????????????
            // ????????????(??????https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_1)
            // ????????????????????????????????????????????????,???????????????201?????????????????????
            WxPayService wxPayService = PayUtils.getWxPayServiceByParam(channel.getParam());
            // ????????????
            WxPayUnifiedOrderRequest orderRequest = new WxPayUnifiedOrderRequest();
            // ??????????????????
            orderRequest.setOutTradeNo(order.getNo() + "_" + DateUtil.format(DateUtil.date(), DatePattern.PURE_DATETIME_MS_FORMAT));
            orderRequest.setOpenid(payRequest.getOpenid());
            orderRequest.setTradeType(wxPayService.getConfig().getTradeType());
            orderRequest.setDeviceInfo("WEB");
            // ??????????????????
            orderRequest.setNotifyUrl(channel.getNotifyUrl());
            orderRequest.setBody(order.getGoodsDetail());
            orderRequest.setProductId(order.getId().toString());
            // ?????????????????????,????????????????????????
            orderRequest.setAttach(payOrder.getId().toString());
            // ????????????
            orderRequest.setTotalFee(order.getTotalPrice().multiply(new BigDecimal(100)).intValue());
            orderRequest.setSpbillCreateIp(HttpContextUtils.getIpAddr(HttpContextUtils.getHttpServletRequest()));
            try {
                return wxPayService.createOrder(orderRequest);
            } catch (WxPayException e) {
                e.printStackTrace();
                throw new OnexException(e.getErrCodeDes());
            }
        } else {
            throw new OnexException("???????????????????????????:" + order.getPayType());
        }
    }

    /**
     * ???????????????
     */
    public boolean cancelAndRefund(ChangeStateForm request) {
        OrderEntity order = getById(request.getId());
        AssertUtils.isNull(order, "???????????????");
        AssertUtils.isFalse(order.isSysCancelable(), "?????????????????????");
        AssertUtils.isFalse(order.isSysRefundable(), "?????????????????????");

        boolean ret = update().eq("id", request.getId()).set("state", ShopConst.OrderStateEnum.CANCELED.value()).update(new OrderEntity());
        AssertUtils.isFalse(ret, "????????????????????????");

        // ????????????
        /*try {
            WxPayRefundResult result = getWxService("WX_PAY_CFG_FISH").refund(request);
            return new Result<>().success(result);
        } catch (WxPayException e) {
            e.printStackTrace();
            throw new OnexException(ErrorCode.WX_API_ERROR, e);
        }*/
        // ????????????
        return false;
    }

    /**
     * ??????
     */
    public boolean refund(Long id) {
        OrderEntity order = getById(id);
        // ????????????
        AssertUtils.isNull(order, ErrorCode.DB_RECORD_NOT_EXISTED);
        // ????????????????????????
        AssertUtils.isFalse(order.getPayState() == ShopConst.OrderPayStateEnum.PAID.value(), "?????????????????????????????????");
        // ??????payOrder
        com.nb6868.onex.portal.modules.pay.entity.OrderEntity payOrder = payOrderService.getById(1L);
        AssertUtils.isNull(payOrder, "?????????????????????");

        WxPayService wxPayService = PayUtils.getWxPayServiceByParam(payOrder.getChannelParam());
        // ????????????
        try {
            WxPayRefundRequest refundRequest = new WxPayRefundRequest();
            refundRequest.setTransactionId(payOrder.getTransactionId());
            refundRequest.setTotalFee(payOrder.getTotalFee());
            refundRequest.setRefundFee(payOrder.getTotalFee());
            refundRequest.setOutRefundNo("T_" + payOrder.getOrderNo());
            wxPayService.refund(refundRequest);
            return true;
        } catch (WxPayException e) {
            e.printStackTrace();
            throw new OnexException(e.getErrCodeDes());
        }
    }

    /**
     * ????????????
     *
     * @param payOrder ????????????
     */
    public boolean payNotify(com.nb6868.onex.portal.modules.pay.entity.OrderEntity payOrder) {
        OrderEntity orderEntity = getById(payOrder.getOrderId());
        if (orderEntity != null && orderEntity.getPayState() == ShopConst.OrderPayStateEnum.NO_PAY.value()) {
            // ????????????
            /*MailSendForm smsSendRequest = new MailSendForm();
            smsSendRequest.setMailTo(orderEntity.getReceiverMobile());
            smsSendRequest.setTplCode("FISH_ORDER");
            smsSendRequest.setContentParams(new JSONObject().set("code", orderEntity.getNo()).set("user", orderEntity.getReceiverConsignee()));
            mailLogService.send(smsSendRequest);*/

            // ????????????
            return update().set("state", ShopConst.OrderStateEnum.PAID.value())
                    .set("pay_state", ShopConst.OrderPayStateEnum.PAID.value())
                    .set("pay_price", new BigDecimal(payOrder.getTotalFee()).divide(new BigDecimal(100), RoundingMode.HALF_UP))
                    .set("pay_time", payOrder.getEndTime())
                    .set("pay_type", payOrder.getId())
                    .eq("id", orderEntity.getId())
                    .update(new OrderEntity());
        } else {
            return false;
        }
    }

}
