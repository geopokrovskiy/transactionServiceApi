package com.geopokrovskiy.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class TransferRequestNotFoundException extends ApiException {
    public TransferRequestNotFoundException(String message, String errorCode) {
        super(message, errorCode);
    }
}
