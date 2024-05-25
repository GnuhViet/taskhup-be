package com.taskhub.project.aspect.exception;

import com.taskhub.project.aspect.exception.model.ErrorsData;
import com.taskhub.project.common.Constants;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

@Slf4j
public class ValidateExceptionBuilder {
    @Getter private final List<FieldError> fieldErrors = new ArrayList<>();
    private String message;

    private static final ErrorsData INTERNAL_SERVER_ERROR = new ErrorsData("internal", "INTERNAL_SERVER_ERROR", "Internal server error");

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
        try {
            if (logic.get()) {
                addFieldError(errorsData);
            }
        } catch (Exception e) {
            log.error("Error when validate: {}", logic.toString());
            log.error(e.getMessage(), e);
            addFieldError(INTERNAL_SERVER_ERROR);
        }
        return this;
    }

    public ValidateExceptionBuilder throwIfFails() {
        // get the first code
        if (!fieldErrors.isEmpty()) {
            throw build(Objects.requireNonNull(fieldErrors.get(0).getRejectedValue()).toString());
        }
        return this;
    }

    public ValidateExceptionBuilder message(String message) {
        this.message = message;
        return this;
    }

    public ValidateException build(String code) {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(null, null);
        fieldErrors.forEach(bindingResult::addError);
        return new ValidateException(message, code, bindingResult);
    }

    public boolean isEmptyError() {
        return fieldErrors.isEmpty();
    }
}
