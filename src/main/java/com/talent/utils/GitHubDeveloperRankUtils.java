package com.talent.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.talent.model.bo.*;
import com.talent.model.dto.User;
import com.talent.model.vo.RatingVO;
import com.talent.mq.Producer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class GitHubDeveloperRankUtils {

    @Resource
    private Producer apiProducer;

    private String baseUrl = "https://api.github.com";

    public String makeRequest(String endpoint, String token) {
        String url = StrUtil.format("{}/{}", baseUrl, endpoint);

        HttpResponse response = HttpRequest.get(url)
                .header("Accept", "application/vnd.github.v3+json")
                .header("Authorization", "Bearer " + token)
                .execute();

        if (response.getStatus() == HttpStatus.HTTP_NOT_FOUND) {
            log.info("请求url出错");
        }

        if (response.getStatus() != HttpStatus.HTTP_OK) {
            log.info("爬取失败");
        }

        return response.body();
    }

    public void asynMakeRequest(String endpoint, String token) {
        String url = StrUtil.format("{}/{}", baseUrl, endpoint);
        apiProducer.sendMessageToAPI(url + " " + token);
    }

    public JSONObject getDeveloperInfo (String account, String token) {
        return new JSONObject(makeRequest("users/" + account, token));
    }

    public JSONObject getReadme (String account, String token) {
        return new JSONObject(makeRequest("repos/" + account + "/" + account + "/readme", token));
    }

    public JSONArray getStarredInfo (String account, String token) {
        return new JSONArray(makeRequest("users/" + account + "/starred?&per_page=250", token));
    }

    public JSONArray getReposInfo (String account, String token) {
        return new JSONArray(makeRequest("users/" + account + "/repos?&per_page=250", token));
    }

    public UserRating getUserRating(User user, String token) {
        UserRating userRating = new UserRating(user, getReposInfo(user.getLogin(), token));
        userRating.ratePopularity();
        userRating.rateBacklinks();
        userRating.rateRepoDescription();
        userRating.rateWebpage();
        userRating.rateRepoPopularity();
        userRating.rateBio();
        return userRating;
    }

    public List<RatingVO> getRatingResult(UserRating userRating) {

        List<String> repoDescLength = userRating.getRepos().stream()
                .filter(r -> r.getDescription() == null || r.getDescription().split(" ").length < 5)
                .map(RepoBo::getFullName)
                .collect(Collectors.toList());

        List<String> notExist = Arrays.asList(
                new BackLink("Biography", userRating.getRating().getBioExists()),
                new BackLink("Blog", userRating.getRating().getBlogExists()),
                new BackLink("Location", userRating.getRating().getLocExists()),
                new BackLink("Company Name", userRating.getRating().getCompanyExists())
        ).stream().filter(BackLink::getIsExists).map(BackLink::getType).collect(Collectors.toList());

        List<String> licensing = userRating.getRepos().stream()
                .filter(r -> r.getLicense() == null)
                .map(RepoBo::getFullName)
                .collect(Collectors.toList());

        List<String> archive = userRating.getRepos().stream()
                .filter(r -> {
                    long today = System.currentTimeMillis();
                    long updatedAt = r.getUpdatedAt().getTime();
                    long daysDiff = (today - updatedAt) / (1000 * 60 * 60 * 24);
                    return daysDiff > 240;
                })
                .map(RepoBo::getFullName)
                .collect(Collectors.toList());

        Suggestions suggestions = new Suggestions(repoDescLength, notExist, licensing, archive);

        return ResultFinalizer.finalizeResult(userRating.getRating(), suggestions);
    }

    public Integer getRankingScore(List<RatingVO> ratingVOS) {

        int scoreSum = 0;
        for (int i = 0; i < ratingVOS.size(); i++) {
            if (!ratingVOS.get(i).getPartial()) {
                scoreSum += ratingVOS.get(i).getScore();
            }
        }

        // Increase overall score by 1.08 to improve accuracy
        int calcScore = (int) ((scoreSum / 6.0) * 1.08); // Using 6.0 to ensure double division

        return calcScore > 100 ? 100 : calcScore; // Return 100 if calcScore is greater than 100

    }

    public void fetchTop100Repositories(Integer page, String token) {
        // GitHub API查询，按星星数量降序排列，限制返回前100个结果
        int since = 100 * page;
        String query = "since="+ since +"&per_page=100"; // Stars数大于0
        String url = "/users?" + query;

        apiProducer.sendMessageToAPI(baseUrl + url + " "  + token);
    }

    public void fetchTop100RepositoriesToCSV(String token) {

        List<User> res = new ArrayList<>();

        // GitHub API查询，按星星数量降序排列，限制返回前100个结果
        String query = "q=stars:>9999&order=desc&per_page=100"; // Stars数大于0
        String url = "search/repositories" + "?" + query;

        String body = makeRequest(url, token);


        // 解析JSON数据
        JSONObject jsonObject = new JSONObject(body);
        JSONArray items = jsonObject.getJSONArray("items");
        List<User> developers = new ArrayList<>();
        // 打印Top 100仓库及所有者信息
        for (int i = 0; i < items.size(); i++) {
            JSONObject repo = items.getJSONObject(i);
            JSONObject owner = repo.getJSONObject("owner"); // 获取仓库所有者信息
            String login = owner.getStr("login");
            String followersUrl = owner.getStr("followers_url");

            // 获取owner信息
            JSONObject developerInfo = getDeveloperInfo(login, token);
            developers.add(User.parseUser(developerInfo));
            // 获取follower信息
            String followersStr = HttpRequest.get(followersUrl + "?per_page=20").header("Authorization", "Bearer " + token)
                    .execute()
                    .body();
            JSONArray followersJSONArray = new JSONArray(followersStr);
            for (JSONObject followerJSON : followersJSONArray.jsonIter()) {
                developers.add(User.parseUser(followerJSON));
            }
            // 获取following信息
            String followingStr = HttpRequest.get("https://api.github.com/users/"+login+"/following" + "?per_page=20").header("Authorization", "Bearer " + token)
                    .execute()
                    .body();
            JSONArray followingJSONArray = new JSONArray(followingStr);
            for (JSONObject followingJSON : followingJSONArray.jsonIter()) {
                developers.add(User.parseUser(followingJSON));
            }
            System.out.println(developers.size());
            // 打印所有者信息
        }
        // 文件写入操作
        // 获取项目根目录路径
        String projectRootPath = System.getProperty("user.dir");
        String fileName = projectRootPath + File.separator + "/doc/developer.csv";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            // 写入表头
            writer.write("ID,login,Company,Email,Bio,Blog,Location\n");
            // 写入每个User对象
            for (User developer : developers) {
                StringBuilder developerDataBuilder = new StringBuilder();
                developerDataBuilder
                        .append(developer.getId()).append(",")
                        .append(developer.getLogin()).append(",")
                        .append(developer.getCompany()).append(",")
                        .append(developer.getEmail()).append(",")
                        .append('\"').append(developer.getBio()).append('\"').append(",")
                        .append(developer.getBlog()).append(",")
                        .append(developer.getLocation()).append(",");

                writer.write(developerDataBuilder + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("CSV 文件已生成: ");
    }
}
