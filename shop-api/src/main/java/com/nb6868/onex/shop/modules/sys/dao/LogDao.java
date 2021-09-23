package com.nb6868.onex.shop.modules.sys.dao;

import com.nb6868.onex.common.jpa.BaseDao;
import com.nb6868.onex.shop.modules.sys.entity.LogEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 日志
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
public interface LogDao extends BaseDao<LogEntity> {

}
