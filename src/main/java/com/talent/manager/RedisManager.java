package com.talent.manager;

import com.talent.model.dto.User;
import com.talent.model.vo.RatingResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class RedisManager {
    private static final String USER_CACHE_KEY_PREFIX = "user:";

    private static final long USER_CACHE_TIME_DAYS = 30;

    private static final String DEVELOPER_CACHE_KEY_PREFIX = "developer:";

    private static final long DEVELOPER_CACHE_TIME_MINUTES = 30;

    private static final String RATING_CACHE_KEY_PREFIX = "rating:";

    private static final long RATING_CACHE_TIME_MINUTES = 30;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    // 设置用户信息缓存
    public void cacheUserInfo(String account, User userInfo) {
        cacheInfo(USER_CACHE_KEY_PREFIX, account, userInfo, USER_CACHE_TIME_DAYS, TimeUnit.DAYS);
    }

    public void cacheDeveloperInfo(String account, User userInfo) {
        cacheInfo(DEVELOPER_CACHE_KEY_PREFIX, account, userInfo, DEVELOPER_CACHE_TIME_MINUTES, TimeUnit.MINUTES);
    }

    public void cacheDeveloperRankInfo(String account, RatingResultVO ratingResultVO) {
        cacheInfo(RATING_CACHE_KEY_PREFIX, account, ratingResultVO, RATING_CACHE_TIME_MINUTES, TimeUnit.MINUTES);
    }

    // 设置信息缓存
    public void cacheInfo(String prefix, String account, Object info, long timeout, TimeUnit unit) {
        String key = prefix + account;
        redisTemplate.opsForValue().set(key, info, timeout, unit);
    }

    public Object getInfo(String prefix, String account) {
        String key = prefix + account;
        return redisTemplate.opsForValue().get(key);
    }

    // 获取用户信息缓存
    public Object getUserInfo(String account) {
        return getInfo(USER_CACHE_KEY_PREFIX, account);
    }
    // 获取开发者信息缓存
    public Object getDeveloperInfo(String account) {
        return getInfo(DEVELOPER_CACHE_KEY_PREFIX, account);
    }

    // 获取排名信息缓存
    public Object getDeveloperRankingInfo(String account) {
        return getInfo(RATING_CACHE_KEY_PREFIX, account);
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

    public void removeDeveloperRankingInfo(String account) {
        removeInfo(RATING_CACHE_KEY_PREFIX, account);
    }

}
