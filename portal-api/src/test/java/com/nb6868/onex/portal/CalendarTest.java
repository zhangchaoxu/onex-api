package com.nb6868.onex.portal;

import cn.hutool.core.date.DateUtil;
import com.nb6868.onex.portal.modules.sys.service.CalendarService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 休假日测试
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CalendarTest {

    @Autowired
    CalendarService calendarService;

    @Test
    @DisplayName("从接口获取数据")
    public void syncHoliday() {
        calendarService.syncWithApi(DateUtil.parseDate("2010-01-01"), DateUtil.parseDate("2010-12-31"));
    }

}
