package com.nb6868.onex.portal;

import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nb6868.onex.common.util.JacksonUtils;
import com.nb6868.onex.uc.entity.ParamsEntity;
import com.nb6868.onex.uc.service.ParamsService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("参数测试")
@Slf4j
public class ParamsTest {

    @Autowired
    private ParamsService paramsService;

    @Test
    @DisplayName("查询")
    public void query() {
        String result = paramsService.getSystemContent("COUNTRY_DATA_SAMPLE_TOP");
        log.error("result={}", result);

        String result2 = paramsService.getSystemContent("DATAV_INFO");
        log.error("result2={}", result2);

        JSONObject jsonObject =  JacksonUtils.getMapper().convertValue(result2, JSONObject.class);
        log.error("jsonObject={}", jsonObject);
    }

    @Test
    @DisplayName("插入")
    public void insert() {
        ObjectNode result = JacksonUtils.getMapper().createObjectNode();
        result.put("k", 1);
        log.error("result={}", result);

        ParamsEntity entity = new ParamsEntity();
        entity.setCode("sss");
        entity.setContent(result.asText());
        entity.setType(0);

        paramsService.save(entity);
    }

}
