package com.api.hitshot.presentation;

import com.api.hitshot.application.SiteUseCase;
import com.api.hitshot.application.dto.UrlParts;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.URL;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/badge")
@Validated
public class badgeController {

    private final SiteUseCase siteUseCase;

    
    @GetMapping("/svg")
    public ResponseEntity<String> getBadgeWithIncrement(@URL @RequestParam(value = "url") String url,
                                                        @RequestParam(defaultValue = "#4c1") String color) {

        UrlParts urlParts = siteUseCase.filterUrlParts(url);
        String filteredUrl = urlParts.merge();

        long visitor = siteUseCase.increaseAndGetCount(filteredUrl);
        String logo = siteUseCase.makeSvgLogo(visitor, color);

        return ResponseEntity.ok()
                .headers(httpHeaders -> httpHeaders.add(HttpHeaders.CONTENT_TYPE, "image/svg+xml; charset=utf-8"))
                .body(logo);
    }


    @GetMapping("/hidden")
    public ResponseEntity<Void> increaseCountWithoutLogo(@URL @RequestParam(value = "url") String url) {

        UrlParts urlParts = siteUseCase.filterUrlParts(url);
        String filteredUrl = urlParts.merge();

        siteUseCase.onlyGetVisitorsCount(filteredUrl);

        return ResponseEntity.ok().build();
    }


    @GetMapping("/view/total-count")
    public ResponseEntity<String> getBadgeWithoutIncrement(@URL @RequestParam(value = "url") String url,
                                                           @RequestParam(defaultValue = "#4c1") String color) {

        UrlParts urlParts = siteUseCase.filterUrlParts(url);
        String filteredUrl = urlParts.merge();

        long visitor = siteUseCase.onlyGetVisitorsCount(filteredUrl);
        String logo = siteUseCase.makeSvgLogo(visitor, color);

        return ResponseEntity.ok()
                .headers(httpHeaders -> httpHeaders.add(HttpHeaders.CONTENT_TYPE, "image/svg+xml; charset=utf-8"))
                .body(logo);
    }


}
