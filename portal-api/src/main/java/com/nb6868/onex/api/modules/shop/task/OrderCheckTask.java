package com.nb6868.onex.api.modules.shop.task;

import com.nb6868.onex.api.modules.shop.service.OrderService;
import com.nb6868.onex.sched.utils.ITask;
import com.nb6868.onex.sched.utils.ScheduleRunResult;
import com.nb6868.onex.sched.utils.TaskInfo;
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
public class OrderCheckTask implements ITask {

    @Autowired
    OrderService orderService;

    @Override
    public ScheduleRunResult run(TaskInfo taskInfo, Long taskLogId) {
        log.debug("OrderCheckTask定时任务正在执行，参数为：{}", taskInfo.getParams());
        orderService.cancelUnPaidOrder(30 * 60);
        return new ScheduleRunResult(true, null);
    }

}
