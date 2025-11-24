package com.geopokrovskiy.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class TransactionNotFoundException extends ApiException{
    public TransactionNotFoundException(String message, String errorCode) {
        super(message, errorCode);
    }
}
