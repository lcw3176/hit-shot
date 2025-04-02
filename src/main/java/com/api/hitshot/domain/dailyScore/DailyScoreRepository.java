package com.api.hitshot.domain.dailyScore;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.util.Optional;

public interface DailyScoreRepository extends MongoRepository<DailyScoreEntity, ObjectId> {

    Optional<DailyScoreEntity> findBySiteIdAndCreatedAtBetween(ObjectId siteId, Instant start, Instant end);
}
