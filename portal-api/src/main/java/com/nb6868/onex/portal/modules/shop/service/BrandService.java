package com.nb6868.onex.portal.modules.shop.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.portal.modules.shop.dao.BrandDao;
import com.nb6868.onex.portal.modules.shop.dto.BrandDTO;
import com.nb6868.onex.portal.modules.shop.entity.BrandEntity;
import com.nb6868.onex.common.pojo.Const;
import com.nb6868.onex.common.jpa.DtoService;
import com.nb6868.onex.common.util.WrapperUtils;
import com.nb6868.onex.common.validator.AssertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Map;

/**
 * 品牌
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class BrandService extends DtoService<BrandDao, BrandEntity, BrandDTO> {

    @Autowired
    private GoodsService goodsService;

    @Override
    public QueryWrapper<BrandEntity> getWrapper(String method, Map<String, Object> params) {
        return new WrapperUtils<BrandEntity>(new QueryWrapper<>(), params)
                .like("name", "name")
                .eq("tenantId", "tenant_id")
                .apply(Const.SQL_FILTER)
                .getQueryWrapper();
    }

    @Override
    public boolean logicDeleteById(Serializable id) {
        AssertUtils.isTrue(goodsService.query().eq("brand_id", id).exists(), "存在商品,不允许删除");
        return super.logicDeleteById(id);
    }

}
