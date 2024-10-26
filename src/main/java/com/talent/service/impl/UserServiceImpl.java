package com.talent.service.impl;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.talent.mapper.UserMapper;
import com.talent.model.dto.User;
import com.talent.service.UserService;
import com.talent.utils.GitHubDeveloperRankUtils;
import org.springframework.stereotype.Service;

/**
* @author yjxx_2022
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2024-10-25 11:53:48
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    @Override
    public User getUserFromGithub(String account, String token) {

        JSONObject developerInfo = GitHubDeveloperRankUtils.getDeveloperInfo(account, token);

        if (developerInfo.get("status") != "404") {
            return null;
        }

        User user = User.parseUser(developerInfo);

        return user;
    }
}




