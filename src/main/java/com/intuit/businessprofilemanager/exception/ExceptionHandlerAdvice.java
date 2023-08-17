package com.intuit.businessprofilemanager.exception;

import com.intuit.businessprofilemanager.model.ErrorResponse;
import com.intuit.businessprofilemanager.model.ValidationResponse;
import com.intuit.businessprofilemanager.utils.AppMetrics;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

@ControllerAdvice
public class ExceptionHandlerAdvice extends ResponseEntityExceptionHandler {
    private HttpHeaders contentType = new HttpHeaders();
    private final AppMetrics metrics;

    public ExceptionHandlerAdvice(AppMetrics metrics) {
        contentType.setContentType(MediaType.APPLICATION_JSON);
        this.metrics = metrics;
    }


    @ExceptionHandler(DataValidationException.class)
    public ResponseEntity<Object> handleInvalidDataException(DataValidationException e) {
        metrics.incrementInvalidDataExceptionCount();
        ErrorResponse errorResponse = ErrorResponse.builder()
                .responseCode(HttpStatus.EXPECTATION_FAILED)
                .responseMessage("Data validation failed")
                .responseDetail(getFailedValidationErrorDetail(e.getFailedValidationResponses()))
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.EXPECTATION_FAILED);
    }

    @ExceptionHandler(ValidationApiFailureException.class)
    public ResponseEntity<Object> handleValidationApiFailureException(ValidationApiFailureException e) {
        metrics.incrementValidationApiFailureCount();
        ErrorResponse errorResponse = ErrorResponse.builder()
                .responseCode(HttpStatus.SERVICE_UNAVAILABLE)
                .responseMessage("Data validation API failure")
                .responseDetail(e.getMessage())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(FutureAwaitingException.class)
    public ResponseEntity<Object> handleUnsuccessfulValidationResponseRetrievalException(FutureAwaitingException e) {
        metrics.incrementValidationApiFailureCount();
        ErrorResponse errorResponse = ErrorResponse.builder()
                .responseCode(HttpStatus.INTERNAL_SERVER_ERROR)
                .responseMessage("Exception caught while waiting for validation result futures.")
                .responseDetail(e.getMessage())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<Object> handleDataNotFoundException(DataNotFoundException e) {
        metrics.incrementDataNotFoundCount();
        ErrorResponse errorResponse = ErrorResponse.builder()
                .responseCode(HttpStatus.NOT_FOUND)
                .responseMessage("Provided profileId is not subscribed or is invalid.")
                .responseDetail(e.getMessage())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RepositoryException.class)
    public ResponseEntity<Object> handleRepositoryException(RepositoryException e) {
        metrics.incrementRepositoryExceptionCount();
        ErrorResponse errorResponse = ErrorResponse.builder()
                .responseCode(HttpStatus.SERVICE_UNAVAILABLE)
                .responseMessage("Exception occurred in repository.")
                .responseDetail(e.getMessage())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .responseCode(HttpStatus.INTERNAL_SERVER_ERROR)
                .responseMessage("An internal server error has occurred.")
                .responseDetail(e.getMessage())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    private String getFailedValidationErrorDetail(List<ValidationResponse> failedValidationResponses) {
        return "Failed to validate profile data for profileId : " + failedValidationResponses.get(0).getProduct();
    }
}
