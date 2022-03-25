package com.nb6868.onex.portal.modules.uc.dao;

import com.nb6868.onex.portal.modules.uc.entity.UserOauthEntity;
import com.nb6868.onex.common.jpa.BaseDao;
import org.apache.ibatis.annotations.Mapper;

/**
 * 第三方用户
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
public interface UserOauthDao extends BaseDao<UserOauthEntity> {

}
