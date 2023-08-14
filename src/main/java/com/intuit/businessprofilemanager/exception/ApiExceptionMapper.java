package com.intuit.businessprofilemanager.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
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
    public ResponseEntity<Object> handleInvalidDataException(InvalidDataException e, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("code", HttpStatus.BAD_REQUEST.value());
        body.put("message", "Failed to validate profile data " + e.getMessage());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException e, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("code", HttpStatus.NOT_FOUND.value());
        body.put("message", "Entity not found in database " + e.getMessage());
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }
}
