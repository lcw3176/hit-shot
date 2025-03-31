package com.api.hitshot.domain.buckets;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class BucketCounter {

    private static final Map<String, Bucket> BUCKET_CACHE = new ConcurrentHashMap<>();

    @Value("${bucket.token-count}")
    private int TOKENS;

    @Scheduled(cron = "0 0 * * * *")
    public void clearBucket() {
        log.info("run bucket cleaner");
        BUCKET_CACHE.clear();
    }

    public Bucket getBucketForDomain(String domain) {
        return BUCKET_CACHE.computeIfAbsent(domain, key -> createNewBucket());
    }

    private Bucket createNewBucket() {
        Bandwidth limit = Bandwidth.builder()
                .capacity(TOKENS)
                .refillGreedy(TOKENS, Duration.ofMinutes(1))
                .build();

        return Bucket.builder().addLimit(limit).build();
    }
}
