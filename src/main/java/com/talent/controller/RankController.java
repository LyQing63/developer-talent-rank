package com.talent.controller;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.talent.common.BaseResponse;
import com.talent.common.ErrorCode;
import com.talent.common.ResultUtils;
import com.talent.manager.RedisManager;
import com.talent.model.bo.UserRating;
import com.talent.model.dto.DeveloperAnalysis;
import com.talent.model.dto.User;
import com.talent.model.vo.RatingResultVO;
import com.talent.model.vo.RatingVO;
import com.talent.service.DeveloperAnalysisService;
import com.talent.service.UserService;
import com.talent.utils.GitHubDeveloperRankUtils;
import com.talent.utils.TokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author 郑皓天
 */
@RestController
@RequestMapping("/rank")
@Slf4j
public class RankController {

    private static final String TOTAL_RANK_KEY = "scrapedDataRanking";

    @Resource
    private RedisManager redisManager;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private DeveloperAnalysisService developerAnalysisService;

    @Resource
    private UserService userService;

    @GetMapping("/score")
    public BaseResponse getRatingResult(String account, @RequestHeader(value = "Authorization") String authorization) {

        String token = TokenUtils.getToken(authorization);
        // 从redis缓存中读取
        RatingResultVO developerRankingInfo = (RatingResultVO) redisManager.getDeveloperRankingInfo(account);
        if (developerRankingInfo != null) {
            return ResultUtils.success(developerRankingInfo);
        }
        // 从数据库中查找
        User developer = null;
        developer = userService.getUserByAccount(account);
        if (developer == null) {
            JSONObject developerInfo = GitHubDeveloperRankUtils.getDeveloperInfo(account, token);
            developer = User.parseUser(developerInfo);
        }
        redisManager.cacheDeveloperInfo(account, developer);
        // 获取各种rate分数
        // 从redis中查找
        RatingResultVO ratingResultVO;
        ratingResultVO = (RatingResultVO) redisManager.getDeveloperRankingInfo(account);
        if (ratingResultVO != null) {
            return ResultUtils.success(ratingResultVO);
        }
        UserRating userRating = GitHubDeveloperRankUtils.getUserRating(developer, token);
        // 分析rate分数
        List<RatingVO> ratingResults = GitHubDeveloperRankUtils.getRatingResult(userRating);
        Integer totalScore = GitHubDeveloperRankUtils.getRankingScore(ratingResults);
        ratingResultVO = new RatingResultVO();
        ratingResultVO.setRatingResults(ratingResults);
        ratingResultVO.setTotalScore(totalScore);

        // 存入数据库中
        DeveloperAnalysis developerAnalysis = DeveloperAnalysis.parseRatingResultVO(developer.getId(), userRating, totalScore);
        boolean save = developerAnalysisService.saveOrUpdate(developerAnalysis);
        if (!save) {
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "存储开发者分析数据出错");
        }

        // 存入缓存中
        redisManager.cacheDeveloperRankInfo(account, ratingResultVO);

        return ResultUtils.success(ratingResultVO);
    }

    @GetMapping("/search100Rank")
    @Profile("dev")
    public BaseResponse search100Rank(@RequestHeader(value = "Authorization") String authorization) {

        String token = TokenUtils.getToken(authorization);
        // GitHub API查询，按星星数量降序排列，限制返回前100个结果
        String query = "q=stars:>9999&order=desc&per_page=100"; // Stars数大于0
        String url = "search/repositories" + "?" + query;

        String body = GitHubDeveloperRankUtils.makeRequest(url, token);


        // 解析JSON数据
        JSONObject jsonObject = new JSONObject(body);
        JSONArray items = jsonObject.getJSONArray("items");
        List<User> developers = new ArrayList<>();
        // 打印Top 100仓库及所有者信息
        for (int i = 0; i < items.size(); i++) {
            JSONObject repo = items.getJSONObject(i);
            JSONObject owner = repo.getJSONObject("owner"); // 获取仓库所有者信息
            String login = owner.getStr("login");

            // 获取owner信息
            JSONObject developerInfo = GitHubDeveloperRankUtils.getDeveloperInfo(login, token);
            User developer = User.parseUser(developerInfo);
            developers.add(developer);

            UserRating userRating = GitHubDeveloperRankUtils.getUserRating(developer, token);
            // 分析rate分数
            List<RatingVO> ratingResults = GitHubDeveloperRankUtils.getRatingResult(userRating);
            Integer totalScore = GitHubDeveloperRankUtils.getRankingScore(ratingResults);
            RatingResultVO ratingResultVO = new RatingResultVO();
            ratingResultVO.setRatingResults(ratingResults);
            ratingResultVO.setTotalScore(totalScore);

            // 存入数据库中
//            DeveloperAnalysis developerAnalysis = DeveloperAnalysis.parseRatingResultVO(developer.getId(), userRating, totalScore);
//            boolean save = developerAnalysisService.saveOrUpdate(developerAnalysis);
//            if (!save) {
//                return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "存储开发者分析数据出错");
//            }
        }
        userService.addUsers(developers);
        return ResultUtils.success(developers);
    }

    private void saveDataToRedis(List<DeveloperAnalysis> dataList) {

        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();

        for (DeveloperAnalysis data : dataList) {
            // 将每条数据按 totalscore 字段排序存入 Redis
            zSetOperations.add(TOTAL_RANK_KEY, data, data.getTotalranking());
        }

        zSetOperations.removeRange(TOTAL_RANK_KEY, 0, -11);
    }

    @GetMapping("/totalRating")
    public BaseResponse getTotalRating() {
        List<User> list = userService.list();
        List<DeveloperAnalysis> developerAnalysisList = new ArrayList<>();
        for (User developer : list) {
            DeveloperAnalysis byId = developerAnalysisService.getById(developer.getId());
            if (byId == null) {
                continue;
            }
            developerAnalysisList.add(byId);
        }
        saveDataToRedis(developerAnalysisList);
        Set<Object> rank = redisTemplate.opsForZSet().reverseRange(TOTAL_RANK_KEY, 0, 9);
        return ResultUtils.success(rank);
    }
}
