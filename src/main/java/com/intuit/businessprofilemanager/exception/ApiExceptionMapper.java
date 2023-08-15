package com.intuit.businessprofilemanager.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class ApiExceptionMapper extends ResponseEntityExceptionHandler {
    private HttpHeaders contentType = new HttpHeaders();

    public ApiExceptionMapper() {
        contentType.setContentType(MediaType.APPLICATION_JSON);
    }

    @ExceptionHandler(InvalidDataException.class)
    public ResponseEntity<Object> handleInvalidDataException(InvalidDataException e) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("code", HttpStatus.BAD_REQUEST.value());
        body.put("message", "Failed to validate profile data. Exception: " + e.getMessage());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ValidationApiFailureException.class)
    public ResponseEntity<Object> handleValidationApiFailureException(ValidationApiFailureException e) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("code", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("message", "Validation api is unavailable. Exception: " + e.getMessage());
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(FutureAwaitingException.class)
    public ResponseEntity<Object> handleUnsuccessfulValidationResponseRetrievalException(FutureAwaitingException e) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("code", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("message", "Exception caught while waiting for validation result futures. Exception: " + e.getMessage());
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<Object> handleDataNotFoundException(DataNotFoundException e) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("code", HttpStatus.NOT_FOUND.value());
        body.put("message", "Provided profileId is not subscribed or is invalid. Exception: " + e.getMessage());
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }
}
