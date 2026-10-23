package com.api.hitshot.presentation.api;

import com.api.hitshot.domain.analytics.VisitEvent;
import com.api.hitshot.domain.analytics.VisitEventRepository;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AnalyticsControllerTest {
    private final VisitEventRepository repository = mock(VisitEventRepository.class);
    private final AnalyticsController controller = new AnalyticsController(repository, "secret");

    @Test
    void recordsVisitWithThirtyDayExpiryAndNoIp() {
        Instant before = Instant.now();
        var response = controller.record(new AnalyticsController.VisitRequest(
                UUID.randomUUID().toString(), "/posts/example/", "application_acme", "https://example.com"), "https://lcw3176.github.io");
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        var capture = org.mockito.ArgumentCaptor.forClass(VisitEvent.class);
        verify(repository).save(capture.capture());
        VisitEvent event = capture.getValue();
        assertEquals("/posts/example/", event.path());
        assertEquals("application_acme", event.campaign());
        assertFalse(event.viewedAt().isBefore(before));
        assertEquals(event.viewedAt().plusSeconds(30L * 24 * 3600), event.expiresAt());
    }

    @Test
    void rejectsQueryInPathAndFullReferrerUrl() {
        String id = UUID.randomUUID().toString();
        assertEquals(HttpStatus.BAD_REQUEST, controller.record(new AnalyticsController.VisitRequest(
                id, "/post?email=abc", null, null), "https://lcw3176.github.io").getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, controller.record(new AnalyticsController.VisitRequest(
                id, "/posts/example/", null, "https://example.com/path"), "https://lcw3176.github.io").getStatusCode());
        verifyNoInteractions(repository);
    }

    @Test
    void rejectsRequestsWithoutBlogOrigin() {
        var visit = new AnalyticsController.VisitRequest(UUID.randomUUID().toString(),
                "/posts/example/", null, null);
        assertEquals(HttpStatus.FORBIDDEN, controller.record(visit, null).getStatusCode());
        assertEquals(HttpStatus.FORBIDDEN, controller.record(visit, "https://other.example").getStatusCode());
        verifyNoInteractions(repository);
    }

    @Test
    void adminReadRequiresToken() {
        assertEquals(HttpStatus.UNAUTHORIZED, controller.list(null, null, 100).getStatusCode());
        assertEquals(HttpStatus.UNAUTHORIZED, controller.list("wrong", null, 100).getStatusCode());
        verifyNoInteractions(repository);
        when(repository.findByOrderByViewedAtDesc(any(Pageable.class))).thenReturn(List.of());
        assertEquals(HttpStatus.OK, controller.list("secret", null, 100).getStatusCode());
    }

    @Test
    void unsetAdminTokenFailsClosed() {
        assertEquals(HttpStatus.UNAUTHORIZED,
                new AnalyticsController(repository, "").list("", null, 100).getStatusCode());
    }
}
