package com.example.palmeiras_api.controller;

import com.example.palmeiras_api.dto.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiError> handleStatus(ResponseStatusException ex, HttpServletRequest request) {
        int status = ex.getStatusCode().value();
        ApiError body = new ApiError(Instant.now(), status, HttpStatus.valueOf(status).getReasonPhrase(), ex.getReason(), request.getRequestURI());
        return ResponseEntity.status(ex.getStatusCode()).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return ResponseEntity.badRequest().body(new ApiError(Instant.now(), 400, "Bad Request", message, request.getRequestURI()));
    }
    @ExceptionHandler({org.springframework.http.converter.HttpMessageNotReadableException.class,org.springframework.web.bind.MissingRequestHeaderException.class,org.springframework.web.bind.MissingServletRequestParameterException.class,org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class,jakarta.validation.ConstraintViolationException.class,org.springframework.data.core.PropertyReferenceException.class})
    public ResponseEntity<ApiError> badInput(Exception ex,HttpServletRequest request){return ResponseEntity.badRequest().body(new ApiError(Instant.now(),400,"Bad Request","Dados, filtro ou ordenação inválidos",request.getRequestURI()));}
    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> conflict(Exception ex,HttpServletRequest request){return ResponseEntity.status(409).body(new ApiError(Instant.now(),409,"Conflict","Recurso possui vínculo ou registro duplicado",request.getRequestURI()));}
}
