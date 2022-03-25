package com.nb6868.onex.portal.modules.crm.dto;

import com.nb6868.onex.common.jpa.Query;
import com.nb6868.onex.common.pojo.PageForm;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "产品查询")
public class ProductQueryFrom extends PageForm {

    @Query(type = Query.Type.LIKE)
    private String name;

    @Query
    private String categoryId;

}
