package com.geopokrovskiy.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class ApiKeyException extends ApiException{
    public ApiKeyException(String message, String errorCode) {
        super(message, errorCode);
    }
}
