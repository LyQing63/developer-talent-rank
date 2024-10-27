package com.talent.model.bo;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.talent.model.dto.User;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class RepoBo {
    private Long id;
    private String nodeId;
    private String name;
    private String fullName;
    private User owner;
    private String description;
    private String homepage;
    private Integer stargazersCount;
    private Integer forksCount;
    private Boolean fork;
    private Boolean isPrivate;
    private String htmlUrl;
    private Date createdAt;
    private Date updatedAt;
    private Date pushedAt;
    private Integer watchersCount;
    private String language;
    private Boolean archived;
    private Integer openIssuesCount;
    private LicenseBo license;
    private Integer forks;
    private Integer openIssues;
    private Integer watchers;
    private String defaultBranch;

    public RepoBo(JSONObject repo) {
        this.id = repo.getLong("id");
        this.nodeId = repo.getStr("node_id");
        this.name = repo.getStr("name");
        this.fullName = repo.getStr("full_name");
        JSONObject ownerJSONObject = repo.getJSONObject("owner");
        this.owner = User.parseUser(ownerJSONObject);
        this.description = repo.getStr("description");
        this.homepage = repo.getStr("homepage");
        this.stargazersCount = repo.getInt("stargazers_count");
        this.forksCount = repo.getInt("forks_count");
        this.fork = repo.getBool("fork");
        this.isPrivate = repo.getBool("private");
        this.htmlUrl = repo.getStr("html_url");
        this.createdAt = repo.getDate("created_at");
        this.updatedAt = repo.getDate("updated_at");
        this.pushedAt = repo.getDate("pushed_at");
        this.watchersCount = repo.getInt("watchers_count");
        this.language = repo.getStr("language");
        this.archived = repo.getBool("archived");
        this.openIssuesCount = repo.getInt("open_issues_count");
        this.forks = repo.getInt("forks");
        this.openIssues = repo.getInt("open_issues");
        this.watchers = repo.getInt("watchers");
        this.defaultBranch = repo.getStr("default_branch");
        this.license = new LicenseBo(repo.getJSONObject("license"));
    }

    public static List<RepoBo> getRepos(JSONArray jsonRepos) {
        List<RepoBo> repos = new ArrayList<>();
        for (JSONObject repoJSON : jsonRepos.jsonIter()) {
            repos.add(new RepoBo(repoJSON));
        }

        return repos;
    }

}
