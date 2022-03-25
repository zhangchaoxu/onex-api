package com.nb6868.onex.portal.modules.shop.dao;

import com.nb6868.onex.portal.modules.shop.entity.OrderEntity;
import com.nb6868.onex.common.jpa.BaseDao;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 订单
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
@Repository("ShopOrderDao")
public interface OrderDao extends BaseDao<OrderEntity> {

}
