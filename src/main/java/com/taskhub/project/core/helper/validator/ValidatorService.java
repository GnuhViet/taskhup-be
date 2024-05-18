package com.taskhub.project.core.helper.validator;

import com.taskhub.project.aspect.exception.ValidateExceptionBuilder;
import com.taskhub.project.aspect.exception.model.ErrorsData;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class ValidatorService {

    private final Validator validator;

    private <T> List<ErrorsData> validate(T object) {

        List<ErrorsData> errors = new ArrayList<>();
        Set<ConstraintViolation<T>> violations = validator.validate(object);

        for (ConstraintViolation<T> violation : violations) {
            var propertyPath = violation.getPropertyPath().toString();
            var message = violation.getMessage();
            errors.add(ErrorsData.builder()
                            .field(propertyPath)
                            .code(propertyPath + ".invalid")
                            .message(message)
                    .build()
            );
        }

        return errors;
    }

    /**
     * Not throw exception if validation fails
     */
    public ValidateExceptionBuilder validate() {
        return new ValidateExceptionBuilder();
    }

    /**
     * Not throw exception if validation fails
     */
    public ValidateExceptionBuilder tryValidate(Object object) {
        ValidateExceptionBuilder exceptionBuilder = new ValidateExceptionBuilder();
        exceptionBuilder.addFieldError(validate(object));
        return exceptionBuilder;
    }

    /**
     * Throw exception if validation fails
     */
    public ValidateExceptionBuilder doValidate(Object object) {
        ValidateExceptionBuilder exceptionBuilder = new ValidateExceptionBuilder();
        exceptionBuilder.addFieldError(validate(object));

        exceptionBuilder.throwIfFails();

        return exceptionBuilder;
    }
}
