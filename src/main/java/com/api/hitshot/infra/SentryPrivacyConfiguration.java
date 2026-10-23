package com.api.hitshot.infra;

import io.sentry.SentryOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SentryPrivacyConfiguration {
    @Bean
    SentryOptions.BeforeSendCallback beforeSendPrivacyFilter() {
        return (event, hint) -> {
            // The analytics request can carry a browser ID and a campaign code.
            // Drop the entire event if it was raised while serving analytics.
            String url = event.getRequest() == null ? null : event.getRequest().getUrl();
            String transaction = event.getTransaction();
            if ((url != null && url.contains("/api/analytics/")) ||
                    (transaction != null && transaction.contains("/api/analytics/"))) return null;

            // A public badge request may also contain a visitor IP or query string.
            event.setRequest(null);
            event.setUser(null);
            event.setBreadcrumbs(null);
            event.setExtras(null);
            event.setTags(null);
            return event;
        };
    }
}
