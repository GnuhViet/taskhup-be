package com.taskhub.project.common.service.model;

import com.taskhub.project.common.Constants;
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


    public static class Code {
        public static final String OK = "01";
        public static final String CREATED = "02";
        public static final String BAD_REQUEST = "03";
        public static final String NOT_FOUND = "04";
        public static final String INTERNAL_SERVER_ERROR = "05";
    }

    public static <T> ServiceResult<T> badRequest() {
        return ServiceResult.<T>builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .code(Code.BAD_REQUEST)
                .build();
    }

    public static <T> ServiceResult<T> notFound() {
        return ServiceResult.<T>builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .code(Code.NOT_FOUND)
                .build();
    }

    public static <T> ServiceResult<T> created(T data) {
        return ServiceResult.<T>builder()
                .httpStatus(HttpStatus.CREATED)
                .serviceStatus(Constants.ServiceStatus.SUCCESS)
                .code(Code.CREATED)
                .data(data)
                .build();
    }

    public static <T> ServiceResult<T> ok(T data) {
        return ServiceResult.<T>builder()
                .httpStatus(HttpStatus.OK)
                .serviceStatus(Constants.ServiceStatus.SUCCESS)
                .code(Code.OK)
                .data(data)
                .build();
    }
}
