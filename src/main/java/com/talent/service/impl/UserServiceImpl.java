package com.talent.service.impl;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.talent.common.ErrorCode;
import com.talent.exception.BusinessException;
import com.talent.manager.RedisManager;
import com.talent.mapper.UserMapper;
import com.talent.model.dto.User;
import com.talent.service.UserService;
import com.talent.utils.GitHubDeveloperRankUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
* @author yjxx_2022
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2024-10-25 11:53:48
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    @Resource
    private RedisManager redisManager;

    @Resource
    private GitHubDeveloperRankUtils gitHubDeveloperRankUtils;

    @Override
    public User getUserFromGithub(String account, String token) {

        JSONObject developerInfo = gitHubDeveloperRankUtils.getDeveloperInfo(account, token);

        if (developerInfo.get("status") != "404") {
            return null;
        }

        User user = User.parseUser(developerInfo);

        return user;
    }

    @Override
    public User getUserInfo(String account, String token) {

        // 从Redis中获取
        Object userCache = redisManager.getUserInfo(account);
        if (userCache == null) {
            // 从数据库中搜索
            QueryWrapper queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("login", account);
            User userSaved = this.getOne(queryWrapper);
            if (userSaved == null) {
                // 数据库中也没有数据
                User userFromGithub = getUserFromGithub(account, token);

                // 不存在这个用户
                if (userFromGithub == null) {
                    return null;
                }

                // 数据库中没有数据，保存
                boolean save = this.save(userFromGithub);
                if (!save) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "数据库存储失败");
                }
                // 再缓存到redis中
                redisManager.cacheDeveloperInfo(account, userFromGithub);

                return null;

            }
            // 从数据库中获取到了
            return userSaved;
        }
        // Redis中获取到了
        return (User) userCache;
    }

    @Override
    public boolean addUsers(List<User> users) {

        if (users.isEmpty()) {
            return false;
        }

        users.forEach(this::saveOrUpdate);

        return true;
    }

    @Override
    public User getUserByAccount(String account) {
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("login", account);
        User one = this.getOne(queryWrapper);
        return one;
    }
}




