package com.api.hitshot.domain.analytics;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface VisitEventRepository extends MongoRepository<VisitEvent, ObjectId> {
    List<VisitEvent> findByOrderByViewedAtDesc(Pageable pageable);
    List<VisitEvent> findByVisitorIdOrderByViewedAtDesc(String visitorId, Pageable pageable);
}
