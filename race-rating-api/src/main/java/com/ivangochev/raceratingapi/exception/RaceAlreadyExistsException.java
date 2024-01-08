package com.ivangochev.raceratingapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class RaceAlreadyExistsException extends RuntimeException{
    public RaceAlreadyExistsException(String message) {
        super(message);
    }
}
