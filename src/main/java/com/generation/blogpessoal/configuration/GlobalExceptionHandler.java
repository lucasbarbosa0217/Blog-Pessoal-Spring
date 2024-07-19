package com.generation.blogpessoal.configuration;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatusException(ResponseStatusException ex, HttpServletRequest request) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("timestamp", LocalDateTime.now().toString());
        responseBody.put("status", ex.getStatusCode().value());
        responseBody.put("error", ((HttpStatus) ex.getStatusCode()).getReasonPhrase());
        responseBody.put("message", ex.getReason());
        responseBody.put("path", request.getRequestURI());
        
        return new ResponseEntity<>(responseBody, ex.getStatusCode());
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
