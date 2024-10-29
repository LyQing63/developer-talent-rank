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
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class GitHubDeveloperRankUtils {

    private static Map<String, Map<String, Integer>> contributionCache = new HashMap<>();
    private static String baseUrl = "https://api.github.com";

    public static String makeRequest(String endpoint, String token) {
        String url = StrUtil.format("{}/{}", baseUrl, endpoint);

        HttpResponse response = HttpRequest.get(url)
                .header("Accept", "application/vnd.github.v3+json")
                .header("Authorization", "Bearer " + token)
                .execute();

        if (response.getStatus() == HttpStatus.HTTP_NOT_FOUND) {
            return null;
        }

        if (response.getStatus() != HttpStatus.HTTP_OK) {
            return null;
        }

        return response.body();
    }

    public static JSONObject getDeveloperInfo (String account, String token) {
        return new JSONObject(makeRequest("users/" + account, token));
    }

    public static JSONArray getStarredInfo (String account, String token) {
        return new JSONArray(makeRequest("users/" + account + "/starred?&per_page=250", token));
    }

    public static JSONArray getReposInfo (String account, String token) {
        return new JSONArray(makeRequest("users/" + account + "/repos?&per_page=250", token));
    }

    public static UserRating getUserRating(User user, String token) {
        UserRating userRating = new UserRating(user, getReposInfo(user.getLogin(), token));
        userRating.ratePopularity();
        userRating.rateBacklinks();
        userRating.rateRepoDescription();
        userRating.rateWebpage();
        userRating.rateRepoPopularity();
        userRating.rateBio();
        return userRating;
    }

    public static List<RatingVO> getRatingResult(UserRating userRating) {

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

    public static Integer getRankingScore(List<RatingVO> ratingVOS) {

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

    public static double calculateProjectImportance(JSONObject repoData) {
        int stars = repoData.getInt("stargazers_count");
        int forks = repoData.getInt("forks_count");
        int watches = 100; // 固定值
        int issues = repoData.getInt("open_issues_count");

        // 加权计算
        return stars * 0.4 + forks * 0.3 + watches * 0.2 + issues * 0.1;
    }

    public static Map<String, Integer> getDeveloperContributions(String username, String repoFullName, String token) {
        String cacheKey = username + "_" + repoFullName;
        if (contributionCache.containsKey(cacheKey)) {
            return contributionCache.get(cacheKey);
        }

        Map<String, Integer> contributions = new HashMap<>();
        contributions.put("commits", 0);
        contributions.put("issues", 0);
        contributions.put("pull_requests", 0);
        contributions.put("reviews", 0);

        String url = "repos/" + repoFullName + "/commits?author=" + username;
        // 获取提交数据
        JSONArray commits = new JSONArray(makeRequest(url, token));
//                            .getJSONArray("commits");
        contributions.put("commits", commits.size());

        // 获取议题和PR数据
        JSONArray issues = new JSONArray(makeRequest(url + "&state=all", token));
//                            .getJSONArray("issues");

        for (JSONObject issue : issues.jsonIter()) {
            if (issue.containsKey("pull_request")) {
                contributions.put("pull_requests", contributions.get("pull_requests") + 1);
            } else {
                contributions.put("issues", contributions.get("issues") + 1);
            }
        }

        contributionCache.put(cacheKey, contributions);
        return contributions;
    }

    public static double calculateTalentRank(String username, String token) {

        String url = "users/" + username + "/repos";

        JSONArray repos = new JSONArray(makeRequest(url, token));
        double totalScore = 0;

        for (JSONObject repo : repos.jsonIter()) {
            // 获取项目重要性
            double repoImportance = calculateProjectImportance(repo);

            // 获取开发者贡献
            Map<String, Integer> contributions = getDeveloperContributions(username, repo.getStr("full_name"), token);

            // 计算贡献度得分
            double contributionScore = contributions.get("commits") * 0.4 +
                    contributions.get("pull_requests") * 0.3 +
                    contributions.get("issues") * 0.2 +
                    contributions.get("reviews") * 0.1;

            // 计算该项目的总得分
            totalScore += repoImportance * contributionScore;
        }

        return totalScore;
    }

    public static String predictNation(JSONObject userData) {
        String username = userData.get("login").toString();

        // 如果用户提供了位置信息
        if (userData.containsKey("location")) {
            return parseLocation(userData.getStr("location"));
        }

        // 构建用户关系网络进行预测
//        JSONArray following = new JSONArray(makeRequest("users/" + username + "/following"));
//        JSONArray followers = new JSONArray(makeRequest("users/" + username + "/followers"));
//
//        Map<String, Integer> nations = new HashMap<>();
//        for (JSONObject user : following.jsonIter()) {
//            JSONObject userDetail = new JSONObject(makeRequest("users/" + user.getStr("login")));
//            if (userDetail.containsKey("location")) {
//                String nation = parseLocation(userDetail.getStr("location"));
//                nations.put(nation, nations.getOrDefault(nation, 0) + 1);
//            }
//        }
//
//        for (JSONObject user : followers.jsonIter()) {
//            JSONObject userDetail = new JSONObject(makeRequest("users/" + user.getStr("login")));
//            if (userDetail.containsKey("location")) {
//                String nation = parseLocation(userDetail.getStr("location"));
//                nations.put(nation, nations.getOrDefault(nation, 0) + 1);
//            }
//        }

//        return nations.entrySet().stream()
//                .max(Map.Entry.comparingByValue())
//                .map(Map.Entry::getKey)
//                .orElse("Unknown");

        return "Unknown";
    }

    private static String parseLocation(String location) {

        if (location == null) {
            return "Unknown";
        }

        location = location.toLowerCase();
        Map<String, List<String>> countryKeywords = new HashMap<>();
        countryKeywords.put("china", Arrays.asList("china", "cn", "beijing", "shanghai", "guangzhou", "shenzhen"));
        countryKeywords.put("usa", Arrays.asList("usa", "us", "united states", "america"));
        countryKeywords.put("japan", Arrays.asList("japan", "jp", "tokyo"));

        for (Map.Entry<String, List<String>> entry : countryKeywords.entrySet()) {
            for (String keyword : entry.getValue()) {
                if (location.contains(keyword)) {
                    return entry.getKey();
                }
            }
        }
        return "Unknown";
    }

    public static List<String> identifyDomains(String username, String token) {
        JSONArray repos = new JSONArray(makeRequest("users/" + username + "/repos", token));
        Map<String, Integer> languages = new HashMap<>();
        Set<String> domains = new HashSet<>();

        // 获取score属性最大的前5个对象
        List<JSONObject> top5 = repos.toList(JSONObject.class).stream()
                .map(JSONObject::new) // 转换为JSONObject
                .sorted((o1, o2) -> Integer.compare(o2.getInt("stargazers_count"), o1.getInt("stargazers_count"))) // 按score降序排序
                .limit(5) // 获取前5个
                .collect(Collectors.toList()); // 收集为List

        for (JSONObject repo : top5) {
            String language = repo.getStr("language");
            if (StrUtil.isNotBlank(language)) {
                languages.put(language, languages.getOrDefault(language, 0) + 1);
            }

            String description = repo.getStr("description", "").toLowerCase();
            JSONArray topics = new JSONObject(makeRequest("repos/" + repo.getStr("full_name") + "/topics", token))
                                    .getJSONArray("names");

            Map<String, List<String>> domainKeywords = new HashMap<>();
            domainKeywords.put("web", Arrays.asList("web", "frontend", "backend", "fullstack"));
            domainKeywords.put("ai", Arrays.asList("machine learning", "deep learning", "ai", "artificial intelligence"));
            domainKeywords.put("mobile", Arrays.asList("ios", "android", "mobile"));
            domainKeywords.put("devops", Arrays.asList("devops", "ci/cd", "kubernetes", "docker"));
            domainKeywords.put("security", Arrays.asList("security", "cryptography", "encryption"));

            for (Map.Entry<String, List<String>> entry : domainKeywords.entrySet()) {
                for (String keyword : entry.getValue()) {
                    if (description.contains(keyword) || topics.contains(keyword)) {
                        domains.add(entry.getKey());
                    }
                }
            }
        }

        // 补充领域判断
        if (!languages.isEmpty()) {
            String primaryLanguage = languages.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse("Unknown");

            Map<String, Set<String>> languageDomainMap = new HashMap<>();
            languageDomainMap.put("JavaScript", new HashSet<>(Collections.singletonList("web")));
            languageDomainMap.put("Python", new HashSet<>(Arrays.asList("ai", "web")));
            languageDomainMap.put("Java", new HashSet<>(Arrays.asList("backend", "mobile")));
            languageDomainMap.put("Swift", new HashSet<>(Collections.singletonList("mobile")));
            languageDomainMap.put("Go", new HashSet<>(Arrays.asList("backend", "devops")));

            if (languageDomainMap.containsKey(primaryLanguage)) {
                domains.addAll(languageDomainMap.get(primaryLanguage));
            }
        }

        return new ArrayList<>(domains);
    }

    public static List<User> fetchTop100Repositories(String token) {

        List<User> res = new ArrayList<>();

        // GitHub API查询，按星星数量降序排列，限制返回前100个结果
        String query = "q=stars:>9999&order=desc&per_page=100"; // Stars数大于0
        String url = "search/repositories" + "?" + query;

        String body = makeRequest(url, token);

        if (body == null) {
            return res;
        }

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
            developers.add(User.parseUser(developerInfo));
            // 打印所有者信息
        }

        return res;
    }

    public static void fetchTop100RepositoriesToCSV(String token) {

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
            JSONObject developerInfo = GitHubDeveloperRankUtils.getDeveloperInfo(login, token);
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
            writer.write("ID,login,Company,Blog,Location,Email,Hireable\n");
            // 写入每个User对象
            for (User developer : developers) {
                StringBuilder developerDataBuilder = new StringBuilder();
                developerDataBuilder
                        .append(developer.getId()).append(",")
                        .append(developer.getLogin()).append(",")
                        .append(developer.getCompany()).append(",")
                        .append(developer.getBlog()).append(",")
                        .append(developer.getBio()).append(",")
                        .append(developer.getLocation()).append(",")
                        .append(developer.getEmail()).append(",")
                        .append(developer.getHireable());

                writer.write(developerDataBuilder + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("CSV 文件已生成: ");
    }
}
