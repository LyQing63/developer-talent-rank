package com.talent.service;

import com.talent.model.dto.User;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author yjxx_2022
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2024-10-25 11:53:48
*/
public interface UserService extends IService<User> {
    User getUserFromGithub(String account, String token);
    User getUserInfo(String account, String token);
    boolean addUsers(List<User> users);
    User getUserByAccount(String account);
}
