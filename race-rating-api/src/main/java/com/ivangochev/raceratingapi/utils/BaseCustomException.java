package com.ivangochev.raceratingapi.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Slf4j
public class BaseCustomException extends RuntimeException {
    public BaseCustomException(String message, HttpStatus httpStatus) {
        super(message);
        log.error("Status: {}, Message: {}", httpStatus, message);
    }
}
