package com.api.hitshot.domain.sites;

import com.api.hitshot.infra.exception.ApiException;
import com.api.hitshot.infra.exception.status.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SiteService {

    private final SiteRepository siteRepository;

    public long readVisitors(String url) {
        SiteEntity site = siteRepository.findByUrl(url)
                .orElseThrow(() -> new ApiException(ErrorCode.SITE_URL_NOT_FOUND));

        return site.getVisitors();
    }


    public ObjectId findIdByUrl(String url) {
        SiteEntity site = siteRepository.findByUrl(url)
                .orElseThrow(() -> new ApiException(ErrorCode.SITE_URL_NOT_FOUND));

        return site.getId();
    }


    public long addVisitors(String url) {
        SiteEntity site = siteRepository.findByUrl(url)
                .orElse(SiteEntity.builder()
                        .url(url)
                        .visitors(0L)
                        .build());

        site.increaseVisitor();

        siteRepository.save(site);

        return site.getVisitors();
    }

}
