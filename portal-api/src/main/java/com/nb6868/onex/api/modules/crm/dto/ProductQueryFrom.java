package com.nb6868.onex.api.modules.crm.dto;

import com.nb6868.onex.common.jpa.Query;
import com.nb6868.onex.common.pojo.PageForm;
import lombok.Data;

@Data
public class ProductQueryFrom extends PageForm {

    @Query(type = Query.Type.INNER_LIKE)
    private String name;

    @Query
    private String categoryId;

}
