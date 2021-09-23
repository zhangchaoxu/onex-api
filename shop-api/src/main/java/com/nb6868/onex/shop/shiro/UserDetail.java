package com.nb6868.onex.shop.shiro;

import com.nb6868.onex.common.auth.AuthProps;
import lombok.Data;

import java.io.Serializable;

/**
 * 登录用户信息
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
public class UserDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String username;
    private String realName;
    private String avatar;
    private Integer gender;
    private String email;
    private String mobile;
    private Integer state;
    private Integer type;
    private Long deptId;
    private String tenantCode;
    /**
     * 登录配置
     */
    private AuthProps.Config loginConfig;

}
