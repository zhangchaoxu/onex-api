package com.nb6868.onex.portal.shiro;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

/**
 * 授权相关
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
public interface ShiroDao {

    /**
     * 通过用户id，获得用户角色列表
     */
    @Select("select role_code from " + ShiroConst.TABLE_USER_ROLE + " where deleted = 0 and user_id = #{userId} GROUP BY role_code")
    List<String> getRoleCodeListByUserId(@Param("userId") Long userId);

    /**
     * 获得所有角色列表
     */
    @Select("select code from " + ShiroConst.TABLE_ROLE + " where deleted = 0 GROUP BY code")
    List<String> getAllRoleCodeList();

    /**
     * 通过用户id，获得用户权限列表
     */
    @Select("SELECT " +
            ShiroConst.TABLE_MENU_SCOPE + ".menu_permissions AS permissions FROM " + ShiroConst.TABLE_MENU_SCOPE +
            " WHERE " + ShiroConst.TABLE_MENU_SCOPE + ".deleted = 0 AND " + ShiroConst.TABLE_MENU_SCOPE + ".menu_permissions != '' AND " + ShiroConst.TABLE_MENU_SCOPE + ".menu_permissions is not null" +
            " AND (("+ ShiroConst.TABLE_MENU_SCOPE +".type = 1  AND " + ShiroConst.TABLE_MENU_SCOPE + ".role_code IN " +
            "( SELECT role_code FROM " + ShiroConst.TABLE_USER_ROLE + " WHERE " + ShiroConst.TABLE_USER_ROLE + ".deleted = 0 AND " + ShiroConst.TABLE_USER_ROLE + ".user_id = #{userId})) OR " +
            "(" + ShiroConst.TABLE_MENU_SCOPE + ".type = 2 AND " + ShiroConst.TABLE_MENU_SCOPE + ".user_id = #{userId}))" +
            " GROUP BY " + ShiroConst.TABLE_MENU_SCOPE + ".menu_id")
    List<String> getPermissionsListByUserId(@Param("userId") Long userId);

    /**
     * 获得所有的权限列表
     */
    @Select("select permissions from " + ShiroConst.TABLE_MENU + " where deleted = 0 and permissions is not null and permissions != '' GROUP BY permissions")
    List<String> getAllPermissionsList();

    /**
     * 通过id获得用户
     */
    @Select("select * from " + ShiroConst.TABLE_USER + " where deleted = 0 and id = #{id} limit 1")
    Map<String, Object> getUserById(@Param("id") Long id);

    @Select("select type, user_id from " + ShiroConst.TABLE_TOKEN + " where deleted = 0 and token = #{token} and expire_time > now() limit 1")
    Map<String, Object> getUserTokenByToken(@Param("token") String token);

    @Update("update " + ShiroConst.TABLE_TOKEN + " set expire_time = DATE_ADD(NOW(), interval #{expireTime} second) where deleted = 0 and token = #{token}")
    boolean updateTokenExpireTime(@Param("token") String token, @Param("expireTime") Integer expireTime);

}
