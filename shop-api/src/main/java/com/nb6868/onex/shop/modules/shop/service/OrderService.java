package com.nb6868.onex.shop.modules.shop.service;

import com.nb6868.onex.common.jpa.EntityService;
import com.nb6868.onex.shop.modules.shop.dao.OrderDao;
import com.nb6868.onex.shop.modules.shop.entity.OrderEntity;
import org.springframework.stereotype.Service;

/**
 * 订单
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class OrderService extends EntityService<OrderDao, OrderEntity> {
}
