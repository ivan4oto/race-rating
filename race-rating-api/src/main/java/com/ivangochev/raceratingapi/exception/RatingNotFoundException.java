package com.ivangochev.raceratingapi.exception;

import com.ivangochev.raceratingapi.utils.BaseCustomException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class RatingNotFoundException extends BaseCustomException {

    public RatingNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
