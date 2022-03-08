package com.nb6868.onex.api.modules.sys.entity;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.annotation.TableName;
import com.nb6868.onex.common.pojo.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 日志
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName(value = "sys_log", autoResultMap = true)
public class LogEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 类型
     */
    private String type;
    /**
     * 内容
     */
    private String content;
    /**
     * 用户操作
     */
    private String operation;
    /**
     * 请求参数
     */
    private String params;
    /**
     * 耗时(毫秒)
     */
    private Long requestTime;
    /**
     * 请求参数
     */
    private JSONObject requestParams;
    /**
     * 状态  0：失败   1：成功
     */
    private Integer state;
    /**
     * 用户名
     */
    private String createName;
}
