package com.api.hitshot.presentation.api;

import com.api.hitshot.application.LogoMaker;
import com.api.hitshot.application.UrlParser;
import com.api.hitshot.application.VisitCounter;
import com.api.hitshot.application.model.SiteVisitor;
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

    private final VisitCounter visitCounter;
    private final UrlParser urlParser;
    private final LogoMaker logoMaker;


    @GetMapping("/svg")
    public ResponseEntity<String> getBadgeWithIncrement(@URL @RequestParam(value = "url") String url,
                                                        @RequestParam(defaultValue = "#4c1") String color) {

        String filteredUrl = urlParser.makeValidFormat(url);

        SiteVisitor visitor = visitCounter.increaseAndGetCount(filteredUrl);
        String logo = logoMaker.makeTodaySvgLogo(visitor.getToday(), visitor.getTotal(), color);

        return ResponseEntity.ok()
                .headers(httpHeaders -> httpHeaders.add(HttpHeaders.CONTENT_TYPE, "image/svg+xml; charset=utf-8"))
                .body(logo);
    }


    @GetMapping("/hidden")
    public ResponseEntity<Void> increaseCountWithoutLogo(@URL @RequestParam(value = "url") String url) {

        String filteredUrl = urlParser.makeValidFormat(url);

        visitCounter.increaseAndGetCount(filteredUrl);

        return ResponseEntity.ok().build();
    }


    @GetMapping("/view/total-count")
    public ResponseEntity<String> getBadgeWithoutIncrement(@URL @RequestParam(value = "url") String url,
                                                           @RequestParam(defaultValue = "#4c1") String color) {

        String filteredUrl = urlParser.makeValidFormat(url);

        long totalVisitor = visitCounter.onlyGetTotalVisitorsCount(filteredUrl);
        String logo = logoMaker.makeOnlyTotalSvgLogo(totalVisitor, color);

        return ResponseEntity.ok()
                .headers(httpHeaders -> httpHeaders.add(HttpHeaders.CONTENT_TYPE, "image/svg+xml; charset=utf-8"))
                .body(logo);
    }


}
