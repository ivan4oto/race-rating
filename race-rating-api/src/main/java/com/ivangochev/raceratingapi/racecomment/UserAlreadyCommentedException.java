package com.ivangochev.raceratingapi.racecomment;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UserAlreadyCommentedException extends RuntimeException{
    public UserAlreadyCommentedException(String message) {
        super(message);
    }
}
