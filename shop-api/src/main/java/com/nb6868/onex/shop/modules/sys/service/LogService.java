package com.nb6868.onex.shop.modules.sys.service;

import com.nb6868.onex.common.jpa.EntityService;
import com.nb6868.onex.shop.modules.sys.dao.LogDao;
import com.nb6868.onex.shop.modules.sys.entity.LogEntity;
import org.springframework.stereotype.Service;

/**
 * 日志
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class LogService extends EntityService<LogDao, LogEntity> {
}
