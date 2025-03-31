package com.api.hitshot.domain.sites;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface SiteRepository extends MongoRepository<SiteEntity, ObjectId> {

    Optional<SiteEntity> findByUrl(String url);
}
