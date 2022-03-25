package com.nb6868.onex.portal.modules.uc.dao;

import com.nb6868.onex.portal.modules.uc.entity.BillEntity;
import com.nb6868.onex.common.jpa.BaseDao;
import org.apache.ibatis.annotations.Mapper;

/**
 * 账单流水
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
public interface BillDao extends BaseDao<BillEntity> {

}
