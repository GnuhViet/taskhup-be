package com.taskhub.project.aspect.exception.model;

import lombok.Builder;

/**
 *
 * @param field: tên tr??ng l?i,
 * @param code: mã l?i
 * @param message: thông báo l?i
 * <p>
 *   ex: class User {name, age} <br>
 *   error name: field = name, code = 01, message = "Name is required" <br>
 *   error age:  field = age, code = 02, message = "Age is required" <br>
 *   error age:  field = age, code = 03, message = "Age is not correct" <br>
 * </p>
 * @author Nguyen Viet Hung
 */
@Builder
public record ErrorsData(String field, String code, String message) {
    public static ErrorsData of(String field, String code, String message) {
        return new ErrorsData(field, code, message);
    }
}