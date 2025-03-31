package com.api.hitshot.infra.client.slack;


import com.api.hitshot.infra.client.ApiHandler;
import com.api.hitshot.infra.client.slack.model.SlackMessage;
import com.api.hitshot.infra.client.slack.util.SlackMessageFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class SlackClient {

    private final WebClient slackRestClient;

    public void sendMessage(String title, String message) {
        SlackMessage slackMessage = SlackMessage.builder()
                .title(title)
                .message(makeTransmissible(message))
                .build();

        sendSlackMessage(slackMessage);
    }

    public void sendMessage(Throwable e) {
        SlackMessage slackMessage = SlackMessage.builder()
                .title(e.getClass().getName())
                .message(makeTransmissible(e))
                .build();

        sendSlackMessage(slackMessage);
    }

    private String makeTransmissible(Throwable e) {
        String stackTrace = Arrays.toString(e.getStackTrace());

        return makeTransmissible(stackTrace);
    }

    private String makeTransmissible(String stackTrace) {
        int len = Math.min(stackTrace.length(), 1700);

        return stackTrace.substring(0, len);
    }

    private void sendSlackMessage(SlackMessage exception) {
        String message = SlackMessageFormatter.makeExceptionMessage(exception);

        ApiHandler.handle(() -> {
            slackRestClient.post()
                    .accept(MediaType.APPLICATION_JSON)
                    .acceptCharset(StandardCharsets.UTF_8)
                    .bodyValue(message)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, response -> response.bodyToMono(String.class)
                            .flatMap(errorBody -> Mono.error(new RuntimeException(errorBody))))
                    .bodyToMono(String.class)
                    .block();

            return null;
        });

    }


}
