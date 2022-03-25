package com.nb6868.onex.portal.modules.uc.service;

import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.portal.modules.uc.dao.TenantDao;
import com.nb6868.onex.portal.modules.uc.dto.TenantDTO;
import com.nb6868.onex.portal.modules.uc.entity.TenantEntity;
import com.nb6868.onex.common.jpa.DtoService;
import com.nb6868.onex.common.util.WrapperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 租户
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class TenantService extends DtoService<TenantDao, TenantEntity, TenantDTO> {

    @Autowired
    UserService userService;

    @Override
    public QueryWrapper<TenantEntity> getWrapper(String method, Map<String, Object> params) {
        return new WrapperUtils<TenantEntity>(new QueryWrapper<>(), params)
                .eq("state", "state")
                .and("search", queryWrapper -> {
                    String search = MapUtil.getStr(params, "search");
                    queryWrapper.like("name", search).or().like("code", search);
                })
                .like("name", "name")
                .getQueryWrapper();
    }

}
