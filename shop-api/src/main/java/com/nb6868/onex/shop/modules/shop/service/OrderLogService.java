package com.nb6868.onex.shop.modules.shop.service;

import com.nb6868.onex.common.jpa.EntityService;
import com.nb6868.onex.shop.modules.shop.dao.OrderLogDao;
import com.nb6868.onex.shop.modules.shop.entity.OrderLogEntity;
import org.springframework.stereotype.Service;

/**
 * 订单记录
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class OrderLogService extends EntityService<OrderLogDao, OrderLogEntity> {
}
