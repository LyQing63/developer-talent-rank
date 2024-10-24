package com.talent.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;

import java.util.*;

public class GitHubDeveloperRank {

    private String githubToken;
    private Map<String, JSONObject> developerCache = new HashMap<>();
    private Map<String, JSONObject> projectCache = new HashMap<>();
    private Map<String, Map<String, Integer>> contributionCache = new HashMap<>();
    private String baseUrl = "https://api.github.com";

    public GitHubDeveloperRank(String githubToken) {
        this.githubToken = githubToken;
    }

    private JSONObject makeRequest(String endpoint) {
        String url = StrUtil.format("{}/{}", baseUrl, endpoint);
        HttpResponse response = HttpRequest.get(url)
                .header("Authorization", "token " + githubToken)
                .header("Accept", "application/vnd.github.v3+json")
                .execute();

        if (response.getStatus() == 403) {
            // 处理速率限制
            String resetTime = response.header("X-RateLimit-Reset");
            long sleepTime = Long.parseLong(resetTime) - System.currentTimeMillis() / 1000;
            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return makeRequest(endpoint);
        }

        return new JSONObject(response.body());
    }

    public double calculateProjectImportance(JSONObject repoData) {
        int stars = repoData.getInt("stargazers_count");
        int forks = repoData.getInt("forks_count");
        int watches = 100; // 固定值
        int issues = repoData.getInt("open_issues_count");

        // 加权计算
        return stars * 0.4 + forks * 0.3 + watches * 0.2 + issues * 0.1;
    }

    public Map<String, Integer> getDeveloperContributions(String username, String repoFullName) {
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
        JSONArray commits = makeRequest("repos/" + repoFullName + "/commits?author=" + username).getJSONArray("commits");
        contributions.put("commits", commits.size());

        // 获取议题和PR数据
        JSONArray issues = makeRequest("repos/" + repoFullName + "/issues?creator=" + username + "&state=all").getJSONArray("issues");
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

    public double calculateTalentRank(String username) {
        JSONArray repos = makeRequest("users/" + username + "/repos").getJSONArray("repos");
        double totalScore = 0;

        for (JSONObject repo : repos.jsonIter()) {
            // 获取项目重要性
            double repoImportance = calculateProjectImportance(repo);

            // 获取开发者贡献
            Map<String, Integer> contributions = getDeveloperContributions(username, repo.getStr("full_name"));

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

    public String predictNation(String username) {
        JSONObject userData = makeRequest("users/" + username);

        // 如果用户提供了位置信息
        if (userData.containsKey("location")) {
            return parseLocation(userData.getStr("location"));
        }

        // 构建用户关系网络进行预测
        JSONArray following = makeRequest("users/" + username + "/following").getJSONArray("following");
        JSONArray followers = makeRequest("users/" + username + "/followers").getJSONArray("followers");

        Map<String, Integer> nations = new HashMap<>();
        for (JSONObject user : following.jsonIter()) {
            JSONObject userDetail = makeRequest("users/" + user.getStr("login"));
            if (userDetail.containsKey("location")) {
                String nation = parseLocation(userDetail.getStr("location"));
                nations.put(nation, nations.getOrDefault(nation, 0) + 1);
            }
        }

        for (JSONObject user : followers.jsonIter()) {
            JSONObject userDetail = makeRequest("users/" + user.getStr("login"));
            if (userDetail.containsKey("location")) {
                String nation = parseLocation(userDetail.getStr("location"));
                nations.put(nation, nations.getOrDefault(nation, 0) + 1);
            }
        }

        return CollUtil.get(nations, nations.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Unknown"));
    }

    private String parseLocation(String location) {
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

    public List<String> identifyDomains(String username) {
        JSONArray repos = makeRequest("users/" + username + "/repos").getJSONArray("repos");
        Map<String, Integer> languages = new HashMap<>();
        Set<String> domains = new HashSet<>();

        for (JSONObject repo : repos.jsonIter()) {
            String language = repo.getStr("language");
            if (StrUtil.isNotBlank(language)) {
                languages.put(language, languages.getOrDefault(language, 0) + 1);
            }

            String description = repo.getStr("description", "").toLowerCase();
            JSONArray topics = makeRequest("repos/" + repo.getStr("full_name") + "/topics").getJSONArray("names");

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
            String primaryLanguage = CollUtil.get(languages, languages.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse(null));

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
