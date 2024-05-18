package com.taskhub.project.common.error.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.taskhub.project.common.Constants;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ApiError {
    private HttpStatus httpStatus;
    private Constants.ServiceStatus serviceStatus;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime timestamp;
    private String message;
    private String debugMessage;
    private List<ApiSubError> subErrors;
    private String code;

    public ApiError() {
        timestamp = LocalDateTime.now();
        serviceStatus = Constants.ServiceStatus.ERROR;
    }

    public ApiError(HttpStatus status) {
        this();
        this.httpStatus = status;
    }

    public ApiError(HttpStatus status, String message, Throwable ex) {
        this();
        this.httpStatus = status;
        this.message = message;
        this.debugMessage = ex.getLocalizedMessage();
    }
}
