package com.api.hitshot.infra.exception.status;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum SuccessCode implements StatusCode {

    OK(HttpStatus.OK, "ok");

    private final HttpStatus httpStatus;
    private final String message;

    private SuccessCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

}
