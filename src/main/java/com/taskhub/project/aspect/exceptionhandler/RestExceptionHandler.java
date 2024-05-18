package com.taskhub.project.aspect.exceptionhandler;

import com.taskhub.project.common.error.model.ApiError;
import com.taskhub.project.common.error.model.ApiValidationError;
import com.taskhub.project.aspect.exception.ValidateException;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
    public static ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(apiError, apiError.getHttpStatus());
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String error = "Malformed JSON request";
        return buildResponseEntity(new ApiError(HttpStatus.BAD_REQUEST, error, ex));
    }

    @ExceptionHandler(MalformedJwtException.class)
    protected ResponseEntity<Object> handleEntityNotFound(MalformedJwtException ex) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
        apiError.setMessage(ex.getMessage());
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> handleEntityNotFound(Exception ex) {
        ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR);
        apiError.setMessage("An error occurred while processing the request");
        return buildResponseEntity(apiError);
    }

    // @ExceptionHandler(NotFoundException.class)
    // protected ResponseEntity<Object> handleNotFoundException(NotFoundException ex) {
    //     ApiError apiError = new ApiError(HttpStatus.NOT_FOUND);
    //     apiError.setMessage(ex.getMessage());
    //     return RestExceptionHandler.buildResponseEntity(apiError);
    // }

    @ExceptionHandler(ValidateException.class)
    public ResponseEntity<Object> handleValidateException(ValidateException ex) {
        var errors = new HashMap<String, List<String>>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();

            errors.computeIfAbsent(fieldName, k -> new ArrayList<>()).add(errorMessage);
        });

        var apiError = new ApiError(HttpStatus.CONFLICT);
        apiError.setMessage(ex.getMessage());
        apiError.setSubErrors(List.of(new ApiValidationError(errors)));
        apiError.setCode(ex.getCode());
        return RestExceptionHandler.buildResponseEntity(apiError);
    }
}
