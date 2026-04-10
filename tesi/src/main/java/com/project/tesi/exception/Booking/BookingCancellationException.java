package com.project.tesi.exception.booking;

import com.project.tesi.exception.common.BaseException;
import org.springframework.http.HttpStatus;

public class BookingCancellationException extends BaseException {
    public BookingCancellationException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
