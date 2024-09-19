package com.ivangochev.raceratingapi.rating;

import com.ivangochev.raceratingapi.utils.BaseCustomException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UserAlreadyVotedException extends BaseCustomException {
    public UserAlreadyVotedException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
