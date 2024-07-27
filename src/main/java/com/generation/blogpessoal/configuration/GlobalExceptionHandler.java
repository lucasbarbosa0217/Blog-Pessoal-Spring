package com.generation.blogpessoal.configuration;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatusException(ResponseStatusException ex, HttpServletRequest request) {
        Map<String, Object> responseBody = new HashMap<>();

        if (ex.getReason() != null) {
            responseBody.put("message", ex.getReason());
        } else if (ex.getMessage() != null) {
            if (ex.getMessage().equals("403 FORBIDDEN")) {
                responseBody.put("message", "Acesso negado");
            } else {
                responseBody.put("message", ex.getMessage());
            }
        } else {
            responseBody.put("message", "");
        }
        responseBody.put("timestamp", LocalDateTime.now().toString());
        responseBody.put("status", ex.getStatusCode().value());
        responseBody.put("error", ((HttpStatus) ex.getStatusCode()).getReasonPhrase());
        responseBody.put("path", request.getRequestURI());

        return new ResponseEntity<>(responseBody, ex.getStatusCode());
    }


    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatusException(BadCredentialsException ex, HttpServletRequest request) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("timestamp", LocalDateTime.now().toString());
        responseBody.put("status", HttpStatus.UNAUTHORIZED.value());
        responseBody.put("message", ex.getMessage());
        responseBody.put("path", request.getRequestURI());
        return new ResponseEntity<>(responseBody, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatusException(HttpMessageNotReadableException ex, HttpServletRequest request) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("timestamp", LocalDateTime.now().toString());
        responseBody.put("status", HttpStatus.BAD_REQUEST.value());
        responseBody.put("message", ex.getMessage());
        responseBody.put("path", request.getRequestURI());
        return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("timestamp", LocalDateTime.now().toString());
        responseBody.put("status", HttpStatus.BAD_REQUEST.value());
        responseBody.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());

        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }

        responseBody.put("message", "Validation error");
        responseBody.put("errors", fieldErrors);
        responseBody.put("path", request.getRequestURI());

        return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
    }
}
