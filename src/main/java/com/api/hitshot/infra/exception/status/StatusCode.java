package com.api.hitshot.infra.exception.status;

import org.springframework.http.HttpStatus;

public interface StatusCode {
    HttpStatus getHttpStatus();

    String getMessage();
}
