package com.nb6868.onex.shop.modules.shop.dao;

import com.nb6868.onex.common.jpa.BaseDao;
import com.nb6868.onex.shop.modules.shop.entity.CartEntity;
import com.nb6868.onex.shop.modules.shop.entity.OrderEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
public interface OrderDao extends BaseDao<OrderEntity> {

}
