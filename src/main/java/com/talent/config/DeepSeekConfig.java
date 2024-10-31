package com.talent.config;

import com.talent.client.DeepSeekClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DeepSeekConfig {

    @Value("${deepSeek.api-key}")
    private String apiKey;

    @Bean
    public DeepSeekClient deepSeekClient() {
        return new DeepSeekClient(apiKey);
    }

}
