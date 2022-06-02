package com.nb6868.onex.portal.excel;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

@Data
public class RegionExcel {

    @Excel(name = "名称")
    private String name;

    @Excel(name = "拼音")
    private String pinyin;

    @Excel(name = "区号")
    private String code;

    @Excel(name = "邮编")
    private String postcode;

}
