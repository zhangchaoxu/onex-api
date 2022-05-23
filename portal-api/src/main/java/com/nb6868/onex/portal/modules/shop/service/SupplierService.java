package com.nb6868.onex.portal.modules.shop.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.portal.modules.shop.entity.SupplierEntity;
import com.nb6868.onex.portal.modules.shop.dao.SupplierDao;
import com.nb6868.onex.portal.modules.shop.dto.SupplierDTO;
import com.nb6868.onex.common.pojo.Const;
import com.nb6868.onex.common.jpa.DtoService;
import com.nb6868.onex.common.util.WrapperUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 供应商
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class SupplierService extends DtoService<SupplierDao, SupplierEntity, SupplierDTO> {

    @Override
    public QueryWrapper<SupplierEntity> getWrapper(String method, Map<String, Object> params) {
        return new WrapperUtils<SupplierEntity>(new QueryWrapper<>(), params)
                .like("name", "name")
                .eq("tenantId", "tenant_id")
                .apply(Const.SQL_FILTER)
                .getQueryWrapper();
    }


}
