package com.api.hitshot.infra;

import io.sentry.SentryEvent;
import io.sentry.protocol.Request;
import io.sentry.protocol.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SentryPrivacyConfigurationTest {
    @Test
    void dropsAnalyticsEvent() {
        SentryEvent event = new SentryEvent();
        Request request = new Request();
        request.setUrl("https://hit.kmapshot.com/api/analytics/visit");
        event.setRequest(request);
        assertNull(new SentryPrivacyConfiguration().beforeSendPrivacyFilter().execute(event, null));
    }

    @Test
    void removesRequestAndUserFromOtherEvents() {
        SentryEvent event = new SentryEvent();
        Request request = new Request();
        request.setUrl("https://hit.kmapshot.com/api/badge/svg?url=secret");
        event.setRequest(request);
        event.setUser(new User());
        SentryEvent result = new SentryPrivacyConfiguration().beforeSendPrivacyFilter().execute(event, null);
        assertNotNull(result);
        assertNull(result.getRequest());
        assertNull(result.getUser());
    }
}
