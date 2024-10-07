package com.ivangochev.raceratingapi.racecomment;

import com.ivangochev.raceratingapi.utils.BaseCustomException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UserAlreadyCommentedException extends BaseCustomException {
    public UserAlreadyCommentedException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
