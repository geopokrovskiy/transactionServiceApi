package com.geopokrovskiy.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UnauthorizedException extends ApiException{
    @Getter
    protected String errorCode;
    public UnauthorizedException(String message) {
        super(message, ErrorCodes.ACCESS_UNAUTHORIZED);
    }

}
