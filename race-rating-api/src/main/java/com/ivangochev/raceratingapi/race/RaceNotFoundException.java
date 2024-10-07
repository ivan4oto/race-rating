package com.ivangochev.raceratingapi.race;

import com.ivangochev.raceratingapi.utils.BaseCustomException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class RaceNotFoundException extends BaseCustomException {

    public RaceNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
