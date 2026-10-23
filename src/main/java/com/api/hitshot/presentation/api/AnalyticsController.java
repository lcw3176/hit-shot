package com.api.hitshot.presentation.api;

import com.api.hitshot.domain.analytics.VisitEvent;
import com.api.hitshot.domain.analytics.VisitEventRepository;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {
    private final VisitEventRepository visits;
    private final String adminToken;
    private final Bucket ingestBucket = Bucket.builder().addLimit(Bandwidth.builder()
            .capacity(300).refillGreedy(300, Duration.ofMinutes(1)).build()).build();

    public AnalyticsController(VisitEventRepository visits,
                               @Value("${analytics.admin-token:}") String adminToken) {
        this.visits = visits;
        this.adminToken = adminToken;
    }

    public record VisitRequest(@NotBlank String visitorId,
                               @NotBlank @Size(max = 500) String path,
                               @Size(max = 64) @Pattern(regexp = "[A-Za-z0-9_-]*") String campaign,
                               @Size(max = 255) String referrerOrigin) {}

    public record VisitResponse(String visitorId, String path, String campaign,
                                String referrerOrigin, Instant viewedAt) {
        static VisitResponse from(VisitEvent event) {
            return new VisitResponse(event.visitorId(), event.path(), event.campaign(),
                    event.referrerOrigin(), event.viewedAt());
        }
    }

    @CrossOrigin(origins = "https://lcw3176.github.io")
    @PostMapping("/visit")
    public ResponseEntity<Void> record(@Valid @RequestBody VisitRequest request,
                                       @RequestHeader(value = "Origin", required = false) String origin) {
        if (!"https://lcw3176.github.io".equals(origin)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        if (!ingestBucket.tryConsume(1)) return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        try {
            UUID.fromString(request.visitorId());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
        // Only store a pathname; query strings can carry personal data.
        if (!request.path().matches("^/posts/[^/?#]{1,300}/$")) {
            return ResponseEntity.badRequest().build();
        }
        String referrer = request.referrerOrigin();
        if (referrer != null && !referrer.isBlank()) {
            try {
                java.net.URI uri = java.net.URI.create(referrer);
                if (!("https".equals(uri.getScheme()) || "http".equals(uri.getScheme())) ||
                        uri.getHost() == null || (uri.getRawPath() != null && !uri.getRawPath().isEmpty()) ||
                        uri.getRawQuery() != null || uri.getRawFragment() != null ||
                        uri.getUserInfo() != null) return ResponseEntity.badRequest().build();
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().build();
            }
        }
        Instant now = Instant.now();
        visits.save(new VisitEvent(null, request.visitorId(), request.path(), request.campaign(),
                referrer, now, now.plus(30, ChronoUnit.DAYS)));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/visits")
    public ResponseEntity<List<VisitResponse>> list(
            @RequestHeader(value = "X-Analytics-Admin-Token", required = false) String token,
            @RequestParam(required = false) String visitorId,
            @RequestParam(defaultValue = "100") int limit) {
        if (adminToken.isBlank() || token == null ||
                !MessageDigest.isEqual(adminToken.getBytes(StandardCharsets.UTF_8),
                        token.getBytes(StandardCharsets.UTF_8))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (limit < 1 || limit > 100) return ResponseEntity.badRequest().build();
        List<VisitEvent> events = visitorId == null || visitorId.isBlank()
                ? visits.findByOrderByViewedAtDesc(PageRequest.of(0, limit))
                : visits.findByVisitorIdOrderByViewedAtDesc(visitorId, PageRequest.of(0, limit));
        return ResponseEntity.ok().cacheControl(CacheControl.noStore())
                .body(events.stream().map(VisitResponse::from).toList());
    }
}
