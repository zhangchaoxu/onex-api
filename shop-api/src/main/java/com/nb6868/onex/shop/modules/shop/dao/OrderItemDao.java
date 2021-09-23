package com.nb6868.onex.shop.modules.shop.dao;

import com.nb6868.onex.common.jpa.BaseDao;
import com.nb6868.onex.shop.modules.shop.entity.CartEntity;
import com.nb6868.onex.shop.modules.shop.entity.OrderItemEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单明细
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
public interface OrderItemDao extends BaseDao<OrderItemEntity> {

}
