package com.taskhub.project.aspect.exception;

import lombok.Getter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;

public class ValidateException extends AuthenticationException {
    @Getter
    private BindingResult bindingResult;

    @Getter
    private String code;

    public ValidateException(
            String message,
            String code,
            BindingResult bindingResult
    ) {
        super(message);
        this.code = code;
        this.bindingResult = bindingResult;
    }
}
