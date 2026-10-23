package com.api.hitshot.domain.analytics;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "visit_events")
public record VisitEvent(@Id ObjectId id, @Indexed String visitorId, String path,
                         String campaign, String referrerOrigin, Instant viewedAt,
                         @Indexed(expireAfter = "0s") Instant expiresAt) {}
