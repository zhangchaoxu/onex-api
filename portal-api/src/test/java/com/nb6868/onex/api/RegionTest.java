package com.nb6868.onex.api;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.poi.excel.ExcelUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.nb6868.onex.api.modules.sys.dao.RegionDao;
import com.nb6868.onex.api.modules.sys.entity.RegionEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * see {https://github.com/xiangyuecn/AreaCity-JsSpider-StatsGov/}
 */
@Slf4j
@DisplayName("行政区域测试")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RegionTest {

    @Resource
    private RegionDao regionDao;

    @Test
    @DisplayName("导入4级区域数据")
    public void importDataLevel4() {
        String filePath = "C:\\Projects\\nb6868\\onex\\ok_data_level4.xlsx";
        ExcelUtil.getReader(filePath).readAll().forEach(map -> {
            RegionEntity entity = new RegionEntity();
            entity.setId(MapUtil.getLong(map, "id"));
            entity.setPid(MapUtil.getLong(map, "pid"));
            entity.setDeep(MapUtil.getInt(map, "deep"));
            entity.setName(MapUtil.getStr(map, "name"));
            entity.setPinyinPrefix(MapUtil.getStr(map, "pinyin_prefix"));
            entity.setPinyin(MapUtil.getStr(map, "pinyin"));
            entity.setExtName(MapUtil.getStr(map, "ext_name"));
            regionDao.insert(entity);
        });
        log.info("4级区域数据-导入完成");
    }

    /**
     * 导入3级区域中心点和边界
     */
    @Test
    @DisplayName("导入3级区域中心点和边界")
    public void importGeo() {
        String filePath = "C:\\Projects\\nb6868\\onex\\ok_geo.xlsx";
        ExcelUtil.getReader(filePath).readAll().forEach(map -> {
            // 判断
            String geo = MapUtil.getStr(map, "geo");
            String polygon = MapUtil.getStr(map, "polygon");
            if (StrUtil.isNotBlank(geo)) {
                regionDao.update(new RegionEntity(), new UpdateWrapper<RegionEntity>()
                        .eq("id", MapUtil.getLong(map, "id"))
                        .set("geo", geo.replace(" ", ","))
                        .set("polygon", polygon));
            }
        });
        log.info("中心点和边界-导入完成");
    }

    /**
     * 站点地址
     * http://datav.aliyun.com/tools/atlas/index.html
     * https://geo.datav.aliyun.com/areas_v3/bound/100000_full.json
     */
    @Test
    @DisplayName("下载json文件")
    public void downloadRegionJson() {
        regionDao.selectList(new QueryWrapper<RegionEntity>().lt("deep", 3)).forEach(regionEntity -> {
            String fileUrl = "https://geo.datav.aliyun.com/areas_v3/bound/";
            if (regionEntity.getDeep() < 3) {
                fileUrl += (StrUtil.fillAfter(regionEntity.getId().toString(), '0', 6) + "_full");
            } else {
                fileUrl += (StrUtil.fillAfter(regionEntity.getId().toString(), '0', 6));
            }
            fileUrl += ".json";
            log.error("download json={}", fileUrl);
            try {
                HttpUtil.downloadFile(fileUrl, FileUtil.file("C:\\Projects\\nb6868\\onex\\geo_json\\" + regionEntity.getId() + ".json"));
            } catch (Exception e) {
                log.error("download json error={}", fileUrl, e);
            }
        });
    }

}
