package com.talent.model.bo;

import cn.hutool.json.JSONArray;
import com.talent.model.dto.User;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
public class UserRating {

    private User user;
    private List<RepoBo> repos;
    private Rating rating = new Rating();

    public UserRating(User user, JSONArray repos) {
        this.user = user;
        this.repos = RepoBo.getRepos(repos);

        int total_stars = this.repos.stream().mapToInt(RepoBo::getStargazersCount).sum();
        int total_forks = this.repos.stream().mapToInt(RepoBo::getForksCount).sum();

        this.user = user;
        this.repos = this.repos.stream().filter((r) -> !Objects.equals(r.getName(), r.getOwner().getLogin()) && !r.getFork())
                    .collect(Collectors.toList());

        this.rating.setTotalStars(total_stars);
        this.rating.setTotalForks(total_forks);
        this.rating.setLocExists(!StringUtils.isAnyBlank(user.getLocation()));
        this.rating.setBlogExists(!StringUtils.isAnyBlank(user.getBlog()));
        this.rating.setBioExists(false);
        this.rating.setCompanyExists(!StringUtils.isAnyBlank(user.getCompany()));
        this.rating.setRepoCount(user.getPublicRepos());
    }

    public void rateBio() {
        // Biography Rating
        if (this.user.getBlog() != null) {
          Integer wordCount = this.user.getBlog().split("").length;

          Integer res = wordCount * 10;
                this.rating.setBioRating(res > 100 ? 100 : res);
        }
    }

    public void ratePopularity() {
        // 计算星级平均数
        double starRate = repos.stream().mapToInt(RepoBo::getStargazersCount).average().orElse(0);

        // 计算流行度分数
        double rate = (double) user.getAccountfollowers() / repos.size() + starRate;
        int result = (int) Math.round(rate * 15);

        // 将结果限制在100以内
        rating.setUserPopularity(Math.min(result, 100));
    }

    public void rateRepoPopularity() {
        // 计算总星数和总分支数
        int totalStars = repos.stream().mapToInt(RepoBo::getStargazersCount).sum();
        int totalForks = repos.stream().mapToInt(RepoBo::getForksCount).sum();

        double rate = (totalStars + totalForks * 1.2) / repos.size();
        int result = (int) Math.round(rate * 16);

        // 限制结果不超过100
        rating.setRepoPopularity(Math.min(result, 100));
    }

    public void rateRepoDescription() {
        // 计算描述字数大于4的仓库
        long repoDescLength = repos.stream()
                .filter(r -> r.getDescription() != null && r.getDescription().split(" ").length > 4)
                .count();

        double rate = (double) repos.size() / repoDescLength;
        int result = (int) Math.round(100 / rate);

        rating.setRepoDescriptionRating(Math.min(result, 100));
    }

    public void rateWebpage() {
        // 计算拥有网页的仓库数量
        long webpageExist = repos.stream()
                .filter(r -> r.getHomepage() != null && !r.getHomepage().isEmpty())
                .count();

        double rate = ((double) webpageExist / repos.size()) * 100;
        int result = (int) Math.round(rate * 1.8);

        rating.setWebpageRating(Math.min(result, 100));
    }

    public void rateBacklinks() {
        // 计算具有反向链接的数量
        int bio = rating.getBioExists() ? 1 : 0;
        int loc = rating.getLocExists() ? 1 : 0;
        int blog = rating.getBlogExists() ? 1 : 0;
        int company = rating.getCompanyExists() ? 1 : 0;

        double rate = (bio + loc + blog + company) / 4.0;
        rating.setBacklinkRating((int) rate * 100);
    }

}
