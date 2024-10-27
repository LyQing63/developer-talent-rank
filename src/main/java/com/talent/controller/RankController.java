package com.talent.controller;

import cn.hutool.json.JSONObject;
import com.talent.common.BaseResponse;
import com.talent.common.ErrorCode;
import com.talent.common.ResultUtils;
import com.talent.model.dto.User;
import com.talent.model.vo.RatingResultVO;
import com.talent.model.vo.RatingVO;
import com.talent.utils.GitHubDeveloperRankUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/rank")
@Slf4j
public class RankController {

    @GetMapping("/score")
    private BaseResponse getScore(String account, @RequestHeader(value = "Authorization") String authorization) {
        String[] s = authorization.split(" ");

        if (s.length < 2) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "未登录");
        }

        String token = s[1];
        if (StringUtils.isAnyBlank(token)) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "token 为空");
        }

        JSONObject developerInfo = GitHubDeveloperRankUtils.getDeveloperInfo(account, token);
        User developer = User.parseUser(developerInfo);

        RatingResultVO ratingResultVO = new RatingResultVO();

        List<RatingVO> ratingResult = GitHubDeveloperRankUtils.getRatingResult(developer, token);
        ratingResultVO.setRatingResults(ratingResult);

        Integer totalScore = GitHubDeveloperRankUtils.getRankingScore(ratingResult);
        ratingResultVO.setTotalScore(totalScore);
        return ResultUtils.success(ratingResultVO);

    }

}
