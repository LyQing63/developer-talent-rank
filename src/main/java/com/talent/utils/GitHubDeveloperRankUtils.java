package com.talent.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.talent.model.bo.DeveloperSearchResult;
import com.talent.model.dto.User;
import lombok.extern.slf4j.Slf4j;

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

    public static PriorityQueue<DeveloperSearchResult> fetchTop100Repositories(String token) {
        // GitHub API查询，按星星数量降序排列，限制返回前100个结果
        String query = "q=stars:>9999&order=desc&per_page=10"; // Stars数大于0
        String url = "search/repositories" + "?" + query;

        // 使用最小堆存储
        PriorityQueue<DeveloperSearchResult> queue = new PriorityQueue<>(
                Comparator.comparing(DeveloperSearchResult::getRanking));

        String body = makeRequest(url, token);

        if (body == null) {
            return null;
        }

        // 解析JSON数据
        JSONObject jsonObject = new JSONObject(body);
        JSONArray items = jsonObject.getJSONArray("items");

        // 打印Top 100仓库及所有者信息
        for (int i = 0; i < items.size(); i++) {
            JSONObject repo = items.getJSONObject(i);
            JSONObject owner = repo.getJSONObject("owner"); // 获取仓库所有者信息
            // 打印所有者信息
            String login = owner.getStr("login");

            DeveloperSearchResult developer = new DeveloperSearchResult();

            Double rank = calculateTalentRank(login, token);
            String location = predictNation(owner);
            List<String> domains = identifyDomains(login, token);

            developer.setData(owner.toBean(User.class));
            developer.setRanking(rank);
            developer.setPredictNation(location);
            developer.setDomains(domains);
            log.info("user: " + login);
            log.info("domain -> " + domains);
            log.info("predictNation -> " + location);
            log.info("ranking -> " + rank);
            queue.add(developer);
        }

        return queue;
    }
}
