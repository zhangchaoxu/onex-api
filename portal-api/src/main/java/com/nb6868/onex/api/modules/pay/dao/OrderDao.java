package com.nb6868.onex.api.modules.pay.dao;

import com.nb6868.onex.api.modules.pay.entity.OrderEntity;
import com.nb6868.onex.common.jpa.BaseDao;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 支付订单
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
@Repository("PayOrderDao")
public interface OrderDao extends BaseDao<OrderEntity> {

}
