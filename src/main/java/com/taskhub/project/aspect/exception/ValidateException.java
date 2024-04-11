package com.taskhub.project.aspect.exception;

import lombok.Getter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;

public class ValidateException extends AuthenticationException {
    @Getter
    private BindingResult bindingResult;

    public ValidateException(String message, BindingResult bindingResult) {
        super(message);
        this.bindingResult = bindingResult;
    }
}
