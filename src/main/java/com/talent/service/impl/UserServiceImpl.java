package com.talent.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.talent.model.dto.User;
import com.talent.service.UserService;
import com.talent.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
* @author yjxx_2022
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2024-10-25 11:53:48
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

}




