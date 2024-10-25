package com.talent.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                .antMatchers("/login", "/error").permitAll() // 公共路径
                .anyRequest().authenticated() // 其他请求需要认证
            )
            .oauth2Login(oauth2 -> oauth2
                .defaultSuccessUrl("/login/oauth", true) // 登录成功后的重定向
                .failureUrl("/login?error=true")  // 登录失败的重定向
            );

        return http.build();
    }
}
