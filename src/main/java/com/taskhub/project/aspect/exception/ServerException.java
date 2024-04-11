package com.taskhub.project.aspect.exception;

import lombok.Getter;

@Getter
public class ServerException extends RuntimeException{
    private final String message;

    public ServerException(String message) {
        this.message = message;
    }
}
