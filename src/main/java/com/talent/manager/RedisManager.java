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

    @Resource
    private RedisTemplate<String, User> redisTemplate;

    // 设置用户信息缓存
    public void cacheUserInfo(String account, User userInfo, long timeout, TimeUnit unit) {
        String key = USER_CACHE_KEY_PREFIX + account;
        redisTemplate.opsForValue().set(key, userInfo, timeout, unit);
    }

    // 获取用户信息缓存
    public Object getUserInfo(String account) {
        String key = USER_CACHE_KEY_PREFIX + account;
        return redisTemplate.opsForValue().get(key);
    }

    // 删除用户信息缓存
    public void removeUserInfo(String account) {
        String key = USER_CACHE_KEY_PREFIX + account;
        redisTemplate.delete(key);
    }
}
