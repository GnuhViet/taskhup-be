package com.taskhub.project.aspect.exception;

import lombok.Getter;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.List;

public class ValidateExceptionBuilder {
    @Getter private final List<FieldError> fieldErrors = new ArrayList<>();
    private String message;

    public void addFieldError(String field, String code, String message) {
        fieldErrors.add(new FieldError("registeredRequest", field, code, false, null, null, message));
    }

    public ValidateExceptionBuilder message(String message) {
        this.message = message;
        return this;
    }

    public ValidateException build() {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(null, null);
        fieldErrors.forEach(bindingResult::addError);
        return new ValidateException(message, bindingResult);
    }

    public boolean isEmptyError() {
        return fieldErrors.isEmpty();
    }
}
