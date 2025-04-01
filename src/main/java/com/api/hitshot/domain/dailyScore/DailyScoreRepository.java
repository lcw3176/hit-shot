package com.api.hitshot.domain.dailyScore;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DailyScoreRepository extends MongoRepository<DailyScoreEntity, ObjectId> {

}
