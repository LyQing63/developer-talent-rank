package com.talent.controller;

import cn.hutool.json.JSONObject;
import com.talent.common.BaseResponse;
import com.talent.common.ResultUtils;
import com.talent.manager.RedisManager;
import com.talent.model.dto.User;
import com.talent.model.vo.RatingResultVO;
import com.talent.model.vo.RatingVO;
import com.talent.utils.GitHubDeveloperRankUtils;
import com.talent.utils.TokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/rank")
@Slf4j
public class RankController {

    @Resource
    private RedisManager redisManager;

    @GetMapping("/score")
    public BaseResponse getRatingResult(String account, @RequestHeader(value = "Authorization") String authorization) {

        String token = TokenUtils.getToken(authorization);
        // 从redis缓存中读取
        RatingResultVO developerRankingInfo = (RatingResultVO) redisManager.getDeveloperRankingInfo(account);
        if (developerRankingInfo != null) {
            return ResultUtils.success(developerRankingInfo);
        }

        JSONObject developerInfo = GitHubDeveloperRankUtils.getDeveloperInfo(account, token);
        User developer = User.parseUser(developerInfo);
        List<RatingVO> ratingResults = GitHubDeveloperRankUtils.getRatingResult(developer, token);
        Integer totalScore = GitHubDeveloperRankUtils.getRankingScore(ratingResults);
        RatingResultVO ratingResultVO = new RatingResultVO();
        ratingResultVO.setRatingResults(ratingResults);
        ratingResultVO.setTotalScore(totalScore);

        // 存入缓存中
        redisManager.cacheDeveloperRankInfo(account, ratingResultVO);

        return ResultUtils.success(ratingResultVO);
    }

}
