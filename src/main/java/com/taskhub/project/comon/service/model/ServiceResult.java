package com.taskhub.project.comon.service.model;

import com.taskhub.project.comon.Constants;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Builder
public class ServiceResult<T> {
    HttpStatus httpStatus;
    Constants.ServiceStatus serviceStatus;
    String message;
    String code;
    T data;

    public static <T> ServiceResult<T> badRequest() {
        return ServiceResult.<T>builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .code("BAD_REQUEST")
                .build();
    }

    public static <T> ServiceResult<T> notFound() {
        return ServiceResult.<T>builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .code("NOT_FOUND")
                .build();
    }

    public static <T> ServiceResult<T> created(T data) {
        return ServiceResult.<T>builder()
                .httpStatus(HttpStatus.CREATED)
                .serviceStatus(Constants.ServiceStatus.SUCCESS)
                .code("CREATED")
                .data(data)
                .build();
    }

    public static <T> ServiceResult<T> ok(T data) {
        return ServiceResult.<T>builder()
                .httpStatus(HttpStatus.OK)
                .serviceStatus(Constants.ServiceStatus.SUCCESS)
                .code("OK")
                .data(data)
                .build();
    }
}
