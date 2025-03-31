package com.api.hitshot.infra.exception.status;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode implements StatusCode {

    SITE_URL_NOT_FOUND(HttpStatus.NOT_FOUND, "no exist site url"),
    NOT_VALID_URL(HttpStatus.BAD_REQUEST, "not valid url"),
    RATE_LIMIT_ACTIVATED(HttpStatus.TOO_MANY_REQUESTS, "api call limit"),
    ;

    private final HttpStatus httpStatus;
    private final String message;

    private ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
