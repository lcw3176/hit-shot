package com.api.hitshot.domain.dailyScore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "daily_score")
public class DailyScoreEntity {

    @Id
    private ObjectId id;

    @Indexed
    private ObjectId siteId;

    private Long count;

    private Instant createdAt;

    public LocalDateTime getKoreanTime() {
        ZoneId seoulZone = ZoneId.of("Asia/Seoul");

        return LocalDateTime.ofInstant(this.createdAt, seoulZone);
    }

}
