package com.api.hitshot.presentation.api;

import com.api.hitshot.application.SiteUseCase;
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
@RequestMapping("/api/badge")
@Validated
public class BadgeController {

    private final SiteUseCase siteUseCase;


    @GetMapping("/svg")
    public ResponseEntity<String> getBadgeWithIncrement(@URL @RequestParam(value = "url") String url,
                                                        @RequestParam(defaultValue = "#4c1") String color) {

        String filteredUrl = siteUseCase.urlValidation(url);

        long visitor = siteUseCase.increaseAndGetCount(filteredUrl);
        String logo = siteUseCase.makeSvgLogo(visitor, color);

        return ResponseEntity.ok()
                .headers(httpHeaders -> httpHeaders.add(HttpHeaders.CONTENT_TYPE, "image/svg+xml; charset=utf-8"))
                .body(logo);
    }


    @GetMapping("/hidden")
    public ResponseEntity<Void> increaseCountWithoutLogo(@URL @RequestParam(value = "url") String url) {

        String filteredUrl = siteUseCase.urlValidation(url);

        siteUseCase.increaseAndGetCount(filteredUrl);

        return ResponseEntity.ok().build();
    }


    @GetMapping("/view/total-count")
    public ResponseEntity<String> getBadgeWithoutIncrement(@URL @RequestParam(value = "url") String url,
                                                           @RequestParam(defaultValue = "#4c1") String color) {

        String filteredUrl = siteUseCase.urlValidation(url);

        long visitor = siteUseCase.onlyGetVisitorsCount(filteredUrl);
        String logo = siteUseCase.makeSvgLogo(visitor, color);

        return ResponseEntity.ok()
                .headers(httpHeaders -> httpHeaders.add(HttpHeaders.CONTENT_TYPE, "image/svg+xml; charset=utf-8"))
                .body(logo);
    }


}
