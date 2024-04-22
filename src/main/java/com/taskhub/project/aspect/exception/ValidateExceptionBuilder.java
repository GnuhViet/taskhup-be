package com.taskhub.project.aspect.exception;

import com.taskhub.project.aspect.exception.model.ErrorsData;
import lombok.Getter;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ValidateExceptionBuilder {
    @Getter private final List<FieldError> fieldErrors = new ArrayList<>();
    private String message;

    // @Builder
    // public record ErrorsData(String field, String code, String message) {}
    //
    public void addFieldError(String field, String code, String message) {
        fieldErrors.add(new FieldError("registeredRequest", field, code, false, null, null, message));
    }

    public void addFieldError(ErrorsData fe) {
        fieldErrors.add(new FieldError("registeredRequest", fe.field(), fe.code(), false, null, null, fe.message()));
    }

    public void addFieldError(List<ErrorsData> feList) {
        feList.forEach(this::addFieldError);
    }

    /**
     * Add validation constraint
     *
     * @param logic - should return true if the validation fails
     * @author Nguyen Viet Hung
     * @since 16/04/2024
     */
    public ValidateExceptionBuilder withConstraint(Supplier<Boolean> logic, ErrorsData errorsData) {
        if (logic.get()) {
            addFieldError(errorsData);
        }
        return this;
    }

    public ValidateExceptionBuilder throwIfFails() {
        if (!fieldErrors.isEmpty()) {
            throw build();
        }
        return this;
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
