package com.project.tesi.exception.common;

import org.springframework.http.HttpStatus;

public class ConcurrentUpdateException extends BaseException {
    public ConcurrentUpdateException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
