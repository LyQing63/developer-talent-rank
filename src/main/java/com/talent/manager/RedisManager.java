package com.talent.manager;

import com.talent.model.dto.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class RedisManager {
    private static final String USER_CACHE_KEY_PREFIX = "user:";

    private static final String DEVELOPER_CACHE_KEY_PREFIX = "developer:";

    @Resource
    private RedisTemplate<String, User> redisTemplate;

    // 设置用户信息缓存
    public void cacheUserInfo(String account, User userInfo, long timeout, TimeUnit unit) {
        cacheInfo(USER_CACHE_KEY_PREFIX, account, userInfo, timeout, unit);
    }

    public void cacheDeveloperInfo(String account, User userInfo, long timeout, TimeUnit unit) {
        cacheInfo(DEVELOPER_CACHE_KEY_PREFIX, account, userInfo, timeout, unit);
    }

    // 设置开发者信息缓存
    public void cacheInfo(String prefix, String account, User userInfo, long timeout, TimeUnit unit) {
        String key = prefix + account;
        redisTemplate.opsForValue().set(key, userInfo, timeout, unit);
    }

    public Object getInfo(String prefix, String account) {
        String key = prefix + account;
        return redisTemplate.opsForValue().get(key);
    }

    // 获取用户信息缓存
    public Object getUserInfo(String account) {
        return getInfo(USER_CACHE_KEY_PREFIX, account);
    }
    // 获取用户信息缓存
    public Object getDeveloperInfo(String account) {
        return getInfo(DEVELOPER_CACHE_KEY_PREFIX, account);
    }

    public void removeInfo(String prefix, String account) {
        String key = prefix + account;
        redisTemplate.delete(key);
    }

    // 删除用户信息缓存
    public void removeUserInfo(String account) {
        removeInfo(USER_CACHE_KEY_PREFIX, account);
    }

    public void removeDeveloperInfo(String account) {
        removeInfo(DEVELOPER_CACHE_KEY_PREFIX, account);
    }
}
