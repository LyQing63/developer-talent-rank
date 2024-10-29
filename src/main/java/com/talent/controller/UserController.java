package com.talent.controller;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import com.talent.common.BaseResponse;
import com.talent.common.ErrorCode;
import com.talent.common.ResultUtils;
import com.talent.model.dto.User;
import com.talent.model.vo.UserLoginVO;
import com.talent.service.UserService;
import com.talent.utils.GitHubDeveloperRankUtils;
import com.talent.utils.TokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

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

    @Value("${github.client-id}")
    private String clientId;

    @Value("${github.client-secret}")
    private String clientSecret;

    private final String GITHUB_TOKEN = "https://github.com/login/oauth/access_token";

    @GetMapping("/oauth")
    public BaseResponse<UserLoginVO> login(String code) {

        if (code == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "code不能为空");
        }

        HttpResponse response = HttpRequest.post(GITHUB_TOKEN)
                .setSSLProtocol("TLSv1.2")
                .form("client_id", clientId)
                .form("client_secret", clientSecret)
                .form("code", code)
                .header("Accept", "application/json")
                .execute();

        JSONObject body = new JSONObject(response.body());

        if (body == null) {
            log.info("body ----->" + body);
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "获取 GitHub Token 失败");
        }

        String token = body.getStr("access_token");

        UserLoginVO userLoginVO = new UserLoginVO();
        userLoginVO.setToken(token);

        String userBody = GitHubDeveloperRankUtils.makeRequest("user", token);
        User user1 = User.parseUser(new JSONObject(userBody));
        userLoginVO.setUser(user1);
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
        String token = TokenUtils.getToken(authorization);
        if (StringUtils.isAnyBlank(token)) {
            return ResultUtils.error(ErrorCode.NOT_LOGIN_ERROR, "未登录");
        }
        String body = GitHubDeveloperRankUtils.makeRequest("user", token);
        // 没有拿到user信息
        if (StringUtils.isAnyBlank(body)) {
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "获取github用户数据失败");
        }
        JSONObject userJSON = new JSONObject(body);

        User user = User.parseUser(userJSON);
        return ResultUtils.success(user);
    }

    @GetMapping("/getDeveloper")
    public BaseResponse<User> getDeveloper(@RequestParam(required = false) String login,
                                           @RequestParam(required = false) Long id,
                                           @RequestHeader(value = "Authorization") String authorization) {
        if (StringUtils.isAnyBlank(login) && id == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        User userInfo;
        String token = TokenUtils.getToken(authorization);
        if (!StringUtils.isAnyBlank(login)) {
            userInfo = userService.getUserInfo(login, token);
        } else {
            userInfo = userService.getById(id);
        }

        return ResultUtils.success(userInfo);
    }



}
