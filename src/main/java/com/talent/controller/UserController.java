package com.talent.controller;

import com.talent.common.BaseResponse;
import com.talent.common.ErrorCode;
import com.talent.common.ResultUtils;
import com.talent.manager.RedisManager;
import com.talent.model.dto.User;
import com.talent.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author 郑皓天
 */
@RestController
@RequestMapping("/login")
@Slf4j
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private RedisManager redisManager;

    @GetMapping("/oauth")
    public BaseResponse login(@AuthenticationPrincipal OAuth2User user) {
        User user1 = User.parseUser(user);
        Long id = user1.getId();
        // mysql中查询
        User userSaved = userService.getById(id);
        if (userSaved == null) {
            // 数据库中没有数据，保存
            boolean save = userService.save(user1);
            if (!save) {
                return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "数据库存储失败");
            }
        }

        return ResultUtils.success(user1);
    }


}
