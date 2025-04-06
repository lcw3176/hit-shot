package com.api.hitshot.domain.dailyScore;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class DailyScoreService {

    private final DailyScoreRepository dailyScoreRepository;


    @Transactional(readOnly = true)
    public long readTodayVisitors(ObjectId siteId) {
        Instant now = Instant.now();
        Instant startOfDay = now.atZone(ZoneOffset.UTC).toLocalDate().atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant endOfDay = startOfDay.plus(1, ChronoUnit.DAYS);

        DailyScoreEntity daily = dailyScoreRepository
                .findBySiteIdAndCreatedAtBetween(siteId, startOfDay, endOfDay)
                .orElse(DailyScoreEntity.builder()
                        .siteId(siteId)
                        .count(0L)
                        .createdAt(now)
                        .build());

        return daily.getCount();
    }

    @Transactional
    public long addTodayVisitors(ObjectId siteId) {
        Instant now = Instant.now();
        Instant startOfDay = now.atZone(ZoneOffset.UTC).toLocalDate().atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant endOfDay = startOfDay.plus(1, ChronoUnit.DAYS);

        DailyScoreEntity dailyScore = dailyScoreRepository
                .findBySiteIdAndCreatedAtBetween(siteId, startOfDay, endOfDay)
                .orElse(DailyScoreEntity.builder()
                        .siteId(siteId)
                        .count(0L)
                        .createdAt(now)
                        .build());

        dailyScore.increaseTodayCount();

        dailyScoreRepository.save(dailyScore);

        return dailyScore.getCount();
    }

}
