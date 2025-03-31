package com.api.hitshot.infra.exception;


import com.api.hitshot.infra.client.slack.SlackClient;
import com.api.hitshot.infra.exception.status.StatusCode;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    private final SlackClient slackClient;

    @ExceptionHandler({ConstraintViolationException.class, IllegalStateException.class,
            MethodArgumentTypeMismatchException.class, IllegalArgumentException.class, MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void violationExceptionHandler(Exception e) {
        log.error(e.getMessage(), e);
//        Sentry.captureException(e);
    }

    @ExceptionHandler({ClassCastException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void botExceptionHandler(ClassCastException e) {
        log.error(e.getMessage(), e);
//        Sentry.captureException(e);
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<String> apiExceptionHandler(ApiException e) {
        StatusCode code = e.getCode();
        log.error(code.getMessage(), e);
        slackClient.sendMessage(e);
//        Sentry.captureException(e);

        return ResponseEntity.status(code.getHttpStatus())
                .body(code.getMessage());
    }


    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public void exceptionHandler(Exception e) {
        log.error(e.getMessage(), e);
        slackClient.sendMessage(e);
//        Sentry.captureException(e);
    }
}
