package com.talent.controller;

import com.talent.common.BaseResponse;
import com.talent.common.ErrorCode;
import com.talent.common.ResultUtils;
import com.talent.manager.RedisLimiterManager;
import com.talent.manager.RedisManager;
import com.talent.model.vo.DescriptionVO;
import com.talent.mq.Producer;
import com.talent.utils.TokenUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author 郑皓天
 */
@RestController
@RequestMapping("/ai")
public class AIController {

    @Resource
    private Producer producer;

    @Resource
    private RedisManager redisManager;

    @Resource
    private RedisLimiterManager redisLimiterManager;

    @PostMapping("/description")
    public BaseResponse createDescription(String login, @RequestHeader(value = "Authorization") String authorization) {

        String token = TokenUtils.getToken(authorization);

        if (StringUtils.isAnyBlank(token)) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "未登录");
        }
        redisLimiterManager.doRateLimiter(token);

        String taskId = String.valueOf(System.currentTimeMillis());
        producer.sendMessageToAI(login + " " + token + " " + taskId);

        return ResultUtils.success(taskId);
    }

    @GetMapping("/getDescription")
    public BaseResponse getDescription(String id) {

        if (StringUtils.isAnyBlank(id)) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "缺少任务id");
        }

        DescriptionVO description = (DescriptionVO) redisManager.getTaskStatusInfo(id);
        if (description == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "任务尚未完成");
        }

        return ResultUtils.success(description);
    }

}
