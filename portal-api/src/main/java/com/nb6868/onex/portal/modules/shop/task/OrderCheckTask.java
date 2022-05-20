package com.nb6868.onex.portal.modules.shop.task;

import com.nb6868.onex.portal.modules.shop.service.OrderService;
import com.nb6868.onex.sys.utils.ISchedTask;
import com.nb6868.onex.sys.utils.SchedTask;
import com.nb6868.onex.sys.utils.ScheduleRunResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 订单检查定时任务
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Component("OrderCheckTask")
@Slf4j
public class OrderCheckTask implements ISchedTask {

    @Autowired
    OrderService orderService;

    @Override
    public ScheduleRunResult run(SchedTask taskInfo, Long taskLogId) {
        log.debug("OrderCheckTask定时任务正在执行，参数为：{}", taskInfo.getParams());
        orderService.cancelUnPaidOrder(30 * 60);
        return new ScheduleRunResult(true, null);
    }

}
