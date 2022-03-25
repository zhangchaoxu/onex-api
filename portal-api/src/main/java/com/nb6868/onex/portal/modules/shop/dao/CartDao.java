package com.nb6868.onex.portal.modules.shop.dao;

import com.nb6868.onex.portal.modules.shop.entity.CartEntity;
import com.nb6868.onex.common.jpa.BaseDao;
import org.apache.ibatis.annotations.Mapper;

/**
 * 购物车
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
public interface CartDao extends BaseDao<CartEntity> {

}
