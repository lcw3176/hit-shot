package com.api.hitshot.presentation.api;

import com.api.hitshot.application.LogoMaker;
import com.api.hitshot.application.UrlParser;
import com.api.hitshot.application.VisitCounter;
import com.api.hitshot.application.model.SiteVisitor;
import com.api.hitshot.domain.dailyScore.DailyScoreEntity;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.URL;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/badge")
@Validated
public class BadgeController {

    private final VisitCounter visitCounter;
    private final UrlParser urlParser;
    private final LogoMaker logoMaker;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@URL @RequestParam("url") String url,
                                         @RequestParam(required = false) String path) {
        visitCounter.registerSite(resolveUrl(url, path));
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/svg")
    public ResponseEntity<String> getBadgeWithIncrement(@URL @RequestParam(value = "url") String url,
                                                        @RequestParam(defaultValue = "#4c1") String color,
                                                        @RequestParam(required = false) String path) {

        String filteredUrl = resolveUrl(url, path);

        SiteVisitor visitor = visitCounter.increaseAndGetCount(filteredUrl);
        String logo = logoMaker.makeTodaySvgLogo(visitor.getToday(), visitor.getTotal(), color);

        return ResponseEntity.ok()
                .headers(httpHeaders -> httpHeaders.add(HttpHeaders.CONTENT_TYPE, "image/svg+xml; charset=utf-8"))
                .body(logo);
    }


    @GetMapping("/hidden")
    public ResponseEntity<Void> increaseCountWithoutLogo(@URL @RequestParam(value = "url") String url,
                                                         @RequestParam(required = false) String path) {

        String filteredUrl = resolveUrl(url, path);

        visitCounter.increaseAndGetCount(filteredUrl);

        return ResponseEntity.ok().build();
    }


    @GetMapping("/view/total-count")
    public ResponseEntity<String> getBadgeWithoutIncrement(@URL @RequestParam(value = "url") String url,
                                                           @RequestParam(defaultValue = "#4c1") String color,
                                                           @RequestParam(required = false) String path) {

        String filteredUrl = resolveUrl(url, path);

        SiteVisitor visitor = visitCounter.onlyGetTotalVisitorsCount(filteredUrl);
        String logo = logoMaker.makeTodaySvgLogo(visitor.getToday(), visitor.getTotal(), color);

        return ResponseEntity.ok()
                .headers(httpHeaders -> httpHeaders.add(HttpHeaders.CONTENT_TYPE, "image/svg+xml; charset=utf-8"))
                .body(logo);
    }


    @GetMapping("/stats/daily")
    public ResponseEntity<Map<String, Object>> getDailyStats(@URL @RequestParam(value = "url") String url,
                                                             @RequestParam(defaultValue = "30") int days,
                                                             @RequestParam(required = false) String path) {

        String filteredUrl = resolveUrl(url, path);
        SiteVisitor visitor = visitCounter.onlyGetTotalVisitorsCount(filteredUrl);
        List<DailyScoreEntity> dailyScores = visitCounter.getDailyStats(filteredUrl, days);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");

        Map<String, Long> dailyData = new LinkedHashMap<>();
        for (DailyScoreEntity score : dailyScores) {
            String date = score.getCreatedAt().atZone(ZoneOffset.UTC).format(formatter);
            dailyData.put(date, score.getCount());
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("url", filteredUrl);
        result.put("today", visitor.getToday());
        result.put("total", visitor.getTotal());
        result.put("daily", dailyData);

        return ResponseEntity.ok(result);
    }


    private String resolveUrl(String url, String path) {
        if (path != null && !path.isBlank()) {
            return urlParser.makeValidFormatWithPath(url, path);
        }
        return urlParser.makeValidFormat(url);
    }

}
