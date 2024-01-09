package com.ivangochev.raceratingapi.race;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class RaceNotFoundException extends RuntimeException {

    public RaceNotFoundException(String message) {
        super(message);
    }
}
