package com.nb6868.onex.api.modules.uc.dao;

import com.nb6868.onex.common.jpa.BaseDao;
import com.nb6868.onex.api.modules.uc.entity.UserParamEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户参数
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
public interface UserParamDao extends BaseDao<UserParamEntity> {

}
