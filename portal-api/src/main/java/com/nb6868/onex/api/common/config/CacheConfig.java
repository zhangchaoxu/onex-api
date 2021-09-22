package com.nb6868.onex.api.common.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheFactoryBean;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;

/**
 * 缓存管理器
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * 参数缓存管理器
     * List<Cache>会主动搜索Cache的实现bean，并添加到caches中
     */
    @Bean("cacheManager")
    public SimpleCacheManager cacheManager(List<Cache> caches) {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(caches);
        return cacheManager;
    }

    /**
     * 参数缓存
     */
    @Primary
    @Bean("commonCache")
    public ConcurrentMapCacheFactoryBean commonCache() {
        ConcurrentMapCacheFactoryBean cacheFactoryBean = new ConcurrentMapCacheFactoryBean();
        cacheFactoryBean.setName("common");
        return cacheFactoryBean;
    }

    /**
     * 参数缓存
     */
    @Bean("paramCache")
    public ConcurrentMapCacheFactoryBean paramCache() {
        ConcurrentMapCacheFactoryBean cacheFactoryBean = new ConcurrentMapCacheFactoryBean();
        cacheFactoryBean.setName("param");
        return cacheFactoryBean;
    }

    /**
     * 验证码缓存管理器
     */
    @Bean("captchaCache")
    public ConcurrentMapCacheFactoryBean captchaCache() {
        ConcurrentMapCacheFactoryBean cacheFactoryBean = new ConcurrentMapCacheFactoryBean();
        cacheFactoryBean.setName("captcha");
        return cacheFactoryBean;
    }

    /**
     * Token缓存管理器
     */
    @Bean("dingtalkTokenCache")
    public ConcurrentMapCacheFactoryBean dingtalkTokenCache() {
        ConcurrentMapCacheFactoryBean cacheFactoryBean = new ConcurrentMapCacheFactoryBean();
        cacheFactoryBean.setName("dingtalkToken");
        return cacheFactoryBean;
    }

    @Bean
    @ConditionalOnMissingBean(name = "redisTemplate")
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

    @Bean
    @ConditionalOnMissingBean(StringRedisTemplate.class)
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

}
