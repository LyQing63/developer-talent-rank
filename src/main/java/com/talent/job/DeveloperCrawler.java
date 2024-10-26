package com.talent.job;

import com.talent.model.bo.DeveloperSearchResult;
import com.talent.utils.GitHubDeveloperRankUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.PriorityQueue;

@Slf4j
@Service
public class DeveloperCrawler {

    private static final String ADMIN_TOKEN = "###########################";

    @Scheduled(cron = "0 0 2 * * MON")
    private static void scheduleWeeklyTask() {
        GitHubDeveloperRankUtils.fetchTop100Repositories(ADMIN_TOKEN);
    }

    public static void main(String[] args) {
        PriorityQueue<DeveloperSearchResult> developerSearchResults = GitHubDeveloperRankUtils.fetchTop100Repositories(ADMIN_TOKEN);
        System.out.println(developerSearchResults);
    }

}
