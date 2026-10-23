package com.api.hitshot.infra.exception;


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

    @ExceptionHandler({ConstraintViolationException.class, IllegalStateException.class,
            MethodArgumentTypeMismatchException.class, IllegalArgumentException.class, MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void violationExceptionHandler(Exception e) {
        // Validation messages can embed submitted paths or campaign values.
        log.warn("Invalid request");
    }

    @ExceptionHandler({ClassCastException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void botExceptionHandler(ClassCastException e) {
        log.warn("Resource not found");
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<String> apiExceptionHandler(ApiException e) {
        StatusCode code = e.getCode();
        log.error(code.getMessage(), e);

        return ResponseEntity.status(code.getHttpStatus())
                .body(code.getMessage());
    }


    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public void exceptionHandler(Exception e) {
        // Avoid exporting request data embedded in parser and database exceptions.
        log.error("Request failed: {}", e.getClass().getSimpleName());
    }
}
