package com.ivangochev.raceratingapi.rating;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UserAlreadyVotedException extends RuntimeException{
    public UserAlreadyVotedException(String message) {
        super(message);
    }
}
