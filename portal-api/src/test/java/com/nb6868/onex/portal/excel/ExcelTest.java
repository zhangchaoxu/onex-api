package com.nb6868.onex.portal.excel;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.params.ExcelExportEntity;
import cn.afterturn.easypoi.handler.inter.IExcelExportServer;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.nb6868.onex.common.pojo.Const;
import com.nb6868.onex.common.util.ConvertUtils;
import com.nb6868.onex.sys.entity.RegionEntity;
import com.nb6868.onex.sys.service.RegionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("Excel测试")
@Slf4j
public class ExcelTest {

    @Autowired
    RegionService regionService;

    @Test
    void exportExcel() throws Exception {
        Date start = new Date();
        ExportParams params = new ExportParams("大数据测试", "测试");
        List<ExcelExportEntity> excelParams = new ArrayList<>();
        ExcelExportEntity excelExportEntity = new ExcelExportEntity();
        excelExportEntity.setName("名称");
        excelExportEntity.setKey("");
        excelParams.add(new ExcelExportEntity("名称", "name"));
        excelParams.add(new ExcelExportEntity("拼音", "pinyin"));
        excelParams.add(new ExcelExportEntity("ID", "id"));
        Workbook workbook = ExcelExportUtil.exportBigExcel(params, excelParams, new IExcelExportServer() {

            @Override
            public List<Object> selectListForExcelExport(Object obj, int page) {
                List<RegionEntity> regions = regionService.query().last(StrUtil.format(Const.LIMIT_RANGE_FMT, 1000 * (page - 1), 1000)).list();
                if (CollUtil.isEmpty(regions) || regions.size() < 1000) {
                    return null;
                }
                List<Object> list = new ArrayList<Object>();
                regions.forEach(new Consumer<RegionEntity>() {
                    @Override
                    public void accept(RegionEntity regionEntity) {
                        list.add(ConvertUtils.sourceToTarget(regionEntity, RegionExcel.class));
                    }
                });
                return list;
            }
        }, 10);

        System.out.println(new Date().getTime() - start.getTime());
        File savefile = new File("C:/home/excel/");
        if (!savefile.exists()) {
            savefile.mkdirs();
        }
        FileOutputStream fos = new FileOutputStream("C:/home/excel/ExcelExportBigData.bigDataExport.xlsx");
        workbook.write(fos);
        fos.close();
    }
}
