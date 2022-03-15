package com.nb6868.onex.api.modules.sys.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.api.modules.sys.dao.LogDao;
import com.nb6868.onex.api.modules.sys.dto.LogDTO;
import com.nb6868.onex.api.modules.sys.entity.LogEntity;
import com.nb6868.onex.common.jpa.DtoService;
import com.nb6868.onex.common.log.BaseLogService;
import com.nb6868.onex.common.log.LogBody;
import com.nb6868.onex.common.util.ConvertUtils;
import com.nb6868.onex.common.util.WrapperUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 日志
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class LogService extends DtoService<LogDao, LogEntity, LogDTO> implements BaseLogService {

    @Override
    public QueryWrapper<LogEntity> getWrapper(String method, Map<String, Object> params) {
        return new WrapperUtils<LogEntity>(new QueryWrapper<>(), params)
                // 状态
                .eq("state", "state")
                // 类型
                .eq("type", "type")
                // 用户
                .like("createName", "create_name")
                // 请求uri
                .like("uri", "uri")
                // 创建时间区间
                .ge("startCreateTime", "create_time")
                .le("endCreateTime", "create_time")
                .getQueryWrapper();
    }

    @Override
    public void saveLog(LogBody log) {
        LogEntity logEntity = ConvertUtils.sourceToTarget(log, LogEntity.class);
        save(logEntity);
    }

}
