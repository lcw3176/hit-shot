package com.api.hitshot.application;

import com.api.hitshot.application.model.SiteVisitor;
import com.api.hitshot.domain.buckets.BucketCounter;
import com.api.hitshot.domain.dailyScore.DailyScoreService;
import com.api.hitshot.domain.sites.SiteService;
import com.api.hitshot.infra.exception.ApiException;
import com.api.hitshot.infra.exception.status.ErrorCode;
import io.github.bucket4j.Bucket;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class VisitCounter {

    private final SiteService siteService;
    private final BucketCounter bucketCounter;
    private final DailyScoreService dailyScoreService;

    @Transactional(readOnly = true)
    public SiteVisitor onlyGetTotalVisitorsCount(String url) {
        ObjectId siteId = siteService.findIdByUrl(url);
        long today = dailyScoreService.readTodayVisitors(siteId);
        long total = siteService.readVisitors(url);

        return SiteVisitor.builder()
                .total(total)
                .today(today)
                .build();
    }

    @Transactional
    public SiteVisitor increaseAndGetCount(String url) {
        Bucket bucket = bucketCounter.getBucketForDomain(url);

        if (bucket.tryConsume(1)) {
            ObjectId siteId = siteService.findIdByUrl(url);
            long today = dailyScoreService.addTodayVisitors(siteId);
            long total = siteService.addVisitors(url);

            return SiteVisitor.builder()
                    .total(total)
                    .today(today)
                    .build();
        }

        throw new ApiException(ErrorCode.RATE_LIMIT_ACTIVATED);
    }

}

