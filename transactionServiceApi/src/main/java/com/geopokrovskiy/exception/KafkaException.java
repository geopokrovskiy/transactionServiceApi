package com.geopokrovskiy.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class KafkaException extends ApiException {
    public KafkaException(String message, String errorCode) {
        super(message, errorCode);
    }
}
