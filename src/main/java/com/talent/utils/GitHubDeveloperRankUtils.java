package com.talent.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;

import java.util.*;

public class GitHubDeveloperRankUtils {


    private static Map<String, JSONObject> developerCache = new HashMap<>();
    private static Map<String, JSONObject> projectCache = new HashMap<>();
    private static Map<String, Map<String, Integer>> contributionCache = new HashMap<>();
    private static String baseUrl = "https://api.github.com";

    private static JSONObject makeRequest(String endpoint, String githubToken) {
        String url = StrUtil.format("{}/{}", baseUrl, endpoint);
        HttpResponse response = HttpRequest.get(url)
                .header("Authorization", "token " + githubToken)
                .header("Accept", "application/vnd.github.v3+json")
                .execute();

        if (response.getStatus() == HttpStatus.HTTP_NOT_FOUND) {
            return null;
        }

        if (response.getStatus() != HttpStatus.HTTP_OK) {
            return null;
        }

        return new JSONObject(response.body());
    }

    public static JSONObject getDeveloperInfo (String account) {
        String url = StrUtil.format("{}/{}", baseUrl, "users/");
        HttpResponse response = HttpRequest.get(url+account)
                .header("Accept", "application/vnd.github.v3+json")
                .execute();
        return new JSONObject(response.body());
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

        // 获取提交数据
        JSONArray commits = makeRequest("repos/" + repoFullName + "/commits?author=" + username, token).getJSONArray("commits");
        contributions.put("commits", commits.size());

        // 获取议题和PR数据
        JSONArray issues = makeRequest("repos/" + repoFullName + "/issues?creator=" + username + "&state=all", token).getJSONArray("issues");
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
        JSONArray repos = makeRequest("users/" + username + "/repos", token).getJSONArray("repos");
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

    public static String predictNation(String username, String token) {
        JSONObject userData = makeRequest("users/" + username, token);

        // 如果用户提供了位置信息
        if (userData.containsKey("location")) {
            return parseLocation(userData.getStr("location"));
        }

        // 构建用户关系网络进行预测
        JSONArray following = makeRequest("users/" + username + "/following", token).getJSONArray("following");
        JSONArray followers = makeRequest("users/" + username + "/followers", token).getJSONArray("followers");

        Map<String, Integer> nations = new HashMap<>();
        for (JSONObject user : following.jsonIter()) {
            JSONObject userDetail = makeRequest("users/" + user.getStr("login"), token);
            if (userDetail.containsKey("location")) {
                String nation = parseLocation(userDetail.getStr("location"));
                nations.put(nation, nations.getOrDefault(nation, 0) + 1);
            }
        }

        for (JSONObject user : followers.jsonIter()) {
            JSONObject userDetail = makeRequest("users/" + user.getStr("login"), token);
            if (userDetail.containsKey("location")) {
                String nation = parseLocation(userDetail.getStr("location"));
                nations.put(nation, nations.getOrDefault(nation, 0) + 1);
            }
        }

        return nations.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Unknown");
    }

    private static String parseLocation(String location) {
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
        JSONArray repos = makeRequest("users/" + username + "/repos", token).getJSONArray("repos");
        Map<String, Integer> languages = new HashMap<>();
        Set<String> domains = new HashSet<>();

        for (JSONObject repo : repos.jsonIter()) {
            String language = repo.getStr("language");
            if (StrUtil.isNotBlank(language)) {
                languages.put(language, languages.getOrDefault(language, 0) + 1);
            }

            String description = repo.getStr("description", "").toLowerCase();
            JSONArray topics = makeRequest("repos/" + repo.getStr("full_name") + "/topics", token).getJSONArray("names");

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
}
