package com.nb6868.onex.api.modules.uc.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.api.modules.uc.dao.TokenDao;
import com.nb6868.onex.api.modules.uc.entity.TokenEntity;
import com.nb6868.onex.common.auth.AuthProps;
import com.nb6868.onex.common.pojo.Const;
import com.nb6868.onex.common.jpa.EntityService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


/**
 * 用户Token
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class TokenService extends EntityService<TokenDao, TokenEntity> {

    /**
     * 通过token获取用户id和登录type
     *
     * @return result
     */
    public TokenEntity getUserIdAndTypeByToken(String token) {
        return query().select("user_id", "type").eq("token", token).apply("expire_time > now()").last(Const.LIMIT_ONE).one();
    }

    /**
     * 通过token获取用户id
     *
     * @return result
     */
    public Long getUserIdByToken(String token) {
        return query().select("user_id").eq("token", token).apply("expire_time > now()").last(Const.LIMIT_ONE).oneOpt().map(TokenEntity::getUserId).orElse(null);
    }

    /**
     * 生成token
     *
     * @param userId      用户ID
     * @param loginConfig 登录配置
     * @return result
     */
    public String createToken(Long userId, AuthProps.Config loginConfig) {
        // 当前时间
        Date now = new Date();
        // 过期时间
        Date expireTime = DateUtil.offsetSecond(now, loginConfig.getTokenExpire().intValue());
        // 生成的token
        if (loginConfig.isMultiLogin()) {
            // 支持多点登录
        } else {
            // 不支持多点登录,注销该用户所有token
            deleteTokenByUserId(userId, loginConfig.getType());
        }
        // 不管逻辑，永远都是重新生成一个token
        TokenEntity tokenEntity = new TokenEntity();
        tokenEntity.setUserId(userId);
        tokenEntity.setToken(IdUtil.simpleUUID());
        tokenEntity.setUpdateTime(now);
        tokenEntity.setExpireTime(expireTime);
        tokenEntity.setType(loginConfig.getType());

        // 保存token
        this.save(tokenEntity);

        return tokenEntity.getToken();
    }

    /**
     * token续期
     *
     * @param token  token
     * @param expire 延长时间
     * @return result
     */
    public boolean renewalToken(String token, Integer expire) {
        return update().setSql("expire_time = DATE_ADD(NOW(), interval " + expire + " second)").eq("token", token).update(new TokenEntity());
    }

    /**
     * 注销token
     *
     * @param token 用户token
     * @return result
     */
    public boolean deleteToken(String token) {
        return logicDeleteByWrapper(new QueryWrapper<TokenEntity>().eq("token", token));
    }

    /**
     * 删除用户下所有token
     *
     * @param userId 用户id
     * @param type   登录类型
     * @return result
     */
    public boolean deleteTokenByUserId(Long userId, String type) {
        return logicDeleteByWrapper(new QueryWrapper<TokenEntity>().eq("user_id", userId).eq("type", type));
    }

    /**
     * 删除用户token
     * @param userIds 用户ID数组
     * @return result
     */
    public boolean deleteByUserIds(List<Long> userIds) {
        return logicDeleteByWrapper(new QueryWrapper<TokenEntity>().in("user_id", userIds));
    }

}
