package com.ivangochev.raceratingapi.exception;

import com.ivangochev.raceratingapi.utils.BaseCustomException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicatedUserInfoException extends BaseCustomException {
    public DuplicatedUserInfoException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
