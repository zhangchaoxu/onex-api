package com.nb6868.onex.shop.modules.shop.service;

import com.nb6868.onex.common.jpa.EntityService;
import com.nb6868.onex.shop.modules.shop.dao.OrderItemDao;
import com.nb6868.onex.shop.modules.shop.entity.OrderItemEntity;
import org.springframework.stereotype.Service;

/**
 * 订单明细
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class OrderItemService extends EntityService<OrderItemDao, OrderItemEntity> {
}
