package com.nb6868.onex.api.util;

import com.nb6868.onex.api.modules.uc.UcConst;
import com.nb6868.onex.api.shiro.SecurityUser;
import com.nb6868.onex.api.shiro.UserDetail;

/**
 * 租户检查工具
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class TenantUtils {

    public static boolean checkTenantId(String tenantCode) {
        UserDetail user = SecurityUser.getUser();

        // 如果是超级管理员，则不进行数据过滤
        if (user.getType() <= UcConst.UserTypeEnum.SYSADMIN.value()) {
            return true;
        } else {
            return tenantCode.equalsIgnoreCase(user.getTenantCode());
        }
    }
}
