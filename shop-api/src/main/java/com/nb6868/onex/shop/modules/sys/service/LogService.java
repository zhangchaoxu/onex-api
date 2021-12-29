package com.nb6868.onex.shop.modules.sys.service;

import com.nb6868.onex.common.jpa.EntityService;
import com.nb6868.onex.common.log.BaseLogService;
import com.nb6868.onex.common.log.LogBody;
import com.nb6868.onex.common.util.ConvertUtils;
import com.nb6868.onex.shop.modules.sys.dao.LogDao;
import com.nb6868.onex.shop.modules.sys.entity.LogEntity;
import org.springframework.stereotype.Service;

/**
 * 日志
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class LogService extends EntityService<LogDao, LogEntity> implements BaseLogService {

    @Override
    public void saveToDb(LogBody log) {
        LogEntity logEntity = ConvertUtils.sourceToTarget(log, LogEntity.class);
        save(logEntity);
    }

}
