package com.nb6868.onex.portal.modules.shop.dao;

import com.nb6868.onex.portal.modules.shop.entity.OrderLogEntity;
import com.nb6868.onex.common.jpa.BaseDao;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单记录
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
public interface OrderLogDao extends BaseDao<OrderLogEntity> {

}
