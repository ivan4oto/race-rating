package com.ivangochev.raceratingapi.exception;

import com.ivangochev.raceratingapi.utils.BaseCustomException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class RaceAlreadyExistsException extends BaseCustomException {
    public RaceAlreadyExistsException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
