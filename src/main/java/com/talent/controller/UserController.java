package com.talent.controller;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.talent.common.BaseResponse;
import com.talent.common.ErrorCode;
import com.talent.common.ResultUtils;
import com.talent.manager.RedisManager;
import com.talent.model.dto.User;
import com.talent.model.vo.UserLoginVO;
import com.talent.service.UserService;
import com.talent.utils.GitHubDeveloperRankUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

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

    @Value("github.client-id")
    private String clientId;

    @Value("github.client-secret")
    private String clientSecret;

    private final String GITHUB_TOKEN = "https://github.com/login/oauth/access_token";

    @GetMapping("/oauth")
    public BaseResponse<UserLoginVO> login(String code) {

        if (StringUtils.isAnyBlank(code)) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "code为空");
        }

        JSONObject request = new JSONObject();
        request.putOnce("client-id", clientId);
        request.putOnce("client_secret", clientSecret);
        request.putOnce("code", code);

        HttpResponse response = HttpRequest.post(GITHUB_TOKEN)
                .body(request.toString())
                .header("Accept", "application/json")
                .execute();

        JSONObject body = new JSONObject(response.body());

        String token = body.getStr("access_token");

        UserLoginVO userLoginVO = new UserLoginVO();
        userLoginVO.setToken(token);

        String userBody = GitHubDeveloperRankUtils.makeRequest("users", token);
        User user1 = User.parseUser(new JSONObject(userBody));
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

        return ResultUtils.success(userLoginVO);
    }

    @GetMapping("/currentUser")
    public BaseResponse<User> currentUser(@RequestHeader(value = "Authorization") String authorization) {
        String[] s = authorization.split(" ");

        if (s.length < 2) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "未登录");
        }

        String body = GitHubDeveloperRankUtils.makeRequest("user", s[1]);
        JSONObject user = new JSONObject(body);

        return ResultUtils.success(user.toBean(User.class));
    }

    @GetMapping("/getDeveloper")
    public BaseResponse<User> getDeveloper(String login, @RequestHeader(value = "Authorization") String token) {

        // 从Redis中获取
        Object userCache = redisManager.getUserInfo(login);
        if (userCache == null) {
            // 从数据库中搜索
            QueryWrapper queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("login", login);
            User userSaved = userService.getOne(queryWrapper);
            if (userSaved == null) {
                // 数据库中也没有数据
                User userFromGithub = userService.getUserFromGithub(login, token);
                // 不存在这个用户
                if (userFromGithub == null) {
                    return ResultUtils.error(ErrorCode.PARAMS_ERROR, "用户不存在");
                }

                // 数据库中没有数据，保存
                boolean save = userService.save(userFromGithub);
                if (!save) {
                    return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "数据库存储失败");
                }
                // 再缓存到redis中
                redisManager.cacheDeveloperInfo(login, userFromGithub, 30, TimeUnit.MINUTES);

                return ResultUtils.success(userFromGithub);

            }
            return ResultUtils.success(userSaved);
        }

        return ResultUtils.success((User) userCache);
    }

}
