package com.api.hitshot.infra.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class BucketConfig {

    @Value("${bucket.token-count}")
    private int TOKENS;

    @Bean
    public Bucket getBucket() {
        Bandwidth limit = Bandwidth.builder()
                .capacity(TOKENS)
                .refillGreedy(TOKENS, Duration.ofMinutes(1))
                .build();

        return Bucket.builder().addLimit(limit).build();
    }
}
