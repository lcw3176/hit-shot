package com.api.hitshot.domain.sites;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "sites")
public class SiteEntity {

    @Id
    private ObjectId id;

    @Indexed(unique = true)
    private String url;

    private Long visitors;

    public void increaseVisitor() {
        this.visitors++;
    }

}
