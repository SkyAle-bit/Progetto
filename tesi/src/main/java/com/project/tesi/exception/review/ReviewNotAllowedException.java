package com.project.tesi.exception.review;

import com.project.tesi.exception.common.BaseException;
import org.springframework.http.HttpStatus;

/**
 * Eccezione lanciata quando un cliente tenta di recensire prima del tempo consentito (422 Unprocessable Entity).
 */
public class ReviewNotAllowedException extends BaseException {

    public ReviewNotAllowedException(String message) {
        super(message, HttpStatus.valueOf(422));
    }
}


