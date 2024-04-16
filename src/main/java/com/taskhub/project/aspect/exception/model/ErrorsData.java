package com.taskhub.project.aspect.exception.model;

import lombok.Builder;

@Builder
public record ErrorsData(String field, String code, String message) {
    public static ErrorsData of(String field, String code, String message) {
        return new ErrorsData(field, code, message);
    }
}