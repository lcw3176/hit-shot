package com.api.hitshot.infra.config;

import com.api.hitshot.infra.client.ExternalApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class SlackWebClientConfig {


    private static final long TIMEOUT_MILLIS = 3000;

    @Value("${client.slack.url}")
    private String slackUrl;

    @Bean
    public WebClient slackRestClient() {
        return ExternalApiClient.getClient(slackUrl, TIMEOUT_MILLIS);
    }
}
