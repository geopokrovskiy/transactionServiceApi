package com.geopokrovskiy.errorhandling;

import com.geopokrovskiy.exception.ApiException;
import com.geopokrovskiy.exception.AuthException;
import com.geopokrovskiy.exception.UnauthorizedException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class AppErrorAttributes {

    @ExceptionHandler({AuthException.class, UnauthorizedException.class, ExpiredJwtException.class, SignatureException.class, MalformedJwtException.class})
    public ResponseEntity<Map<String, Object>> handleUnauthorizedExceptions(HttpServletRequest request, Exception ex) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Map<String, Object>> handleApiException(HttpServletRequest request, ApiException ex) {
        Map<String, Object> errorDetails = new LinkedHashMap<>();
        errorDetails.put("code", ex.getErrorCode());
        errorDetails.put("message", ex.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, errorDetails);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(HttpServletRequest request, IllegalArgumentException ex) {
        Map<String, Object> errorDetails = new LinkedHashMap<>();
        errorDetails.put("code", "Invalid input");
        errorDetails.put("message", ex.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, errorDetails);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(HttpServletRequest request, Exception ex) {
        String message = ex.getMessage() != null ? ex.getMessage() : ex.getClass().getName();
        Map<String, Object> errorDetails = new LinkedHashMap<>();
        errorDetails.put("code", "INTERNAL_ERROR");
        errorDetails.put("message", message);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, errorDetails);
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status, String message) {
        Map<String, Object> errorDetails = new LinkedHashMap<>();
        errorDetails.put("message", message);
        return buildErrorResponse(status, errorDetails);
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status, Map<String, Object> errorDetails) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", status.value());
        response.put("errors", new ArrayList<>() {{
            add(errorDetails);
        }});
        return new ResponseEntity<>(response, status);
    }
}