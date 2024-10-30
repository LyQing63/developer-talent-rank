package com.talent.job;

import com.talent.utils.GitHubDeveloperRankUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class DeveloperCrawler {

    private static final String ADMIN_TOKEN = "###################";

    private static Integer countPage = 1;

    @Resource
    private GitHubDeveloperRankUtils gitHubDeveloperRankUtils;

    @Scheduled(cron = "0 0 2 * * MON")
    private void scheduleWeeklyTask() {
        gitHubDeveloperRankUtils.fetchTop100Repositories(countPage++, ADMIN_TOKEN);
    }


}
