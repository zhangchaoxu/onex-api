package com.nb6868.onex.api.modules.common.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

/**
 * 日志Dao
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
public interface LogDao {

    @Insert("INSERT into sys_log(id, type, content, operation, uri, method, params, request_time, user_agent, ip, state, create_name, create_id, create_time, update_id, update_time, deleted) " +
            "values(#{id}, #{type}, #{content}, #{operation}, #{uri}, #{method}, #{params}, #{request_time}, #{user_agent}, #{ip}, #{state}, #{create_name}, #{create_id}, #{create_time}, #{update_id}, #{update_time}, 0)")
    int saveLog(Map<String, Object> map);

}
