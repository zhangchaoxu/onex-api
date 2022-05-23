package com.nb6868.onex.portal.modules.crm.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.portal.modules.crm.dao.ProductDao;
import com.nb6868.onex.portal.modules.crm.dto.ProductDTO;
import com.nb6868.onex.portal.modules.crm.entity.ProductEntity;
import com.nb6868.onex.common.pojo.Const;
import com.nb6868.onex.common.jpa.DtoService;
import com.nb6868.onex.common.util.WrapperUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * CRM产品
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class ProductService extends DtoService<ProductDao, ProductEntity, ProductDTO> {

    @Override
    public QueryWrapper<ProductEntity> getWrapper(String method, Map<String, Object> params) {
        return new WrapperUtils<ProductEntity>(new QueryWrapper<>(), params)
                .like("name", "name")
                .eq("marketable", "marketable")
                .eq("categoryId", "category_id")
                .and("search", queryWrapper -> {
                    String search = (String) params.get("search");
                    queryWrapper.like("name", search).or().like("sn", search).or();
                })
                .apply(Const.SQL_FILTER)
                .getQueryWrapper().orderByAsc("sn");
    }

}
