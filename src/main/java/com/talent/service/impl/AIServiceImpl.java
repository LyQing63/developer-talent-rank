package com.talent.service.impl;

import cn.hutool.core.codec.Base64;
import cn.hutool.json.JSONObject;
import com.talent.client.DeepSeekClient;
import com.talent.constant.DeepSeekConstant;
import com.talent.model.dto.User;
import com.talent.service.AIService;
import com.talent.service.UserService;
import com.talent.utils.GitHubDeveloperRankUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;

@Service
@Slf4j
public class AIServiceImpl implements AIService {

    @Resource
    private GitHubDeveloperRankUtils gitHubDeveloperRankUtils;

    @Resource
    private DeepSeekClient deepSeekClient;

    @Resource
    private UserService userService;

    @Override
    public String getDescription(String account, String token) {

        JSONObject readmeJSON = gitHubDeveloperRankUtils.getReadme(account, token);
        User developerInfo = userService.getUserInfo(account, token);

        String blogUrl = developerInfo.getBlog();
        String readmeBase64 = readmeJSON.getStr("content");

        if (StringUtils.isAnyBlank(blogUrl)) {
            log.info("No blog url");
        }
        if (StringUtils.isAnyBlank(readmeBase64)) {
            log.info("No readme");
        }

        if (StringUtils.isAnyBlank(readmeBase64) && StringUtils.isAnyBlank(blogUrl)) {
            return "这人很懒，没有介绍";
        }

        // BASE64解码
        String readmeContent = Base64.decodeStr(readmeBase64);

        // 获取blog内容
        String blogContent = scrapeBlogContent(blogUrl);

        //询问ai
        deepSeekClient.setSystem(DeepSeekConstant.PROMPT);
        String question = blogContent + "\n" + readmeContent;

        return deepSeekClient.askForward(question);
    }

    private String scrapeBlogContent(String blogUrl) {
        // 添加 "https://" 如果 URL 缺少协议头
        if (blogUrl != null && !blogUrl.isEmpty() && !blogUrl.startsWith("http://") && !blogUrl.startsWith("https://")) {
            blogUrl = "https://" + blogUrl;
        }
        StringBuilder content = new StringBuilder();
        try {
            Document doc = Jsoup.connect(blogUrl)
                    .timeout(600000)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0.0.0 Safari/537.36")
                    .get();
            // 假设博客内容在 <article> 标签中，根据实际情况调整
            for (Element article : doc.select("article")) {
                content.append(article.text()).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString();
    }
}
