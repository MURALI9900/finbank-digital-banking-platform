package com.finbank.transaction.exception;

import com.finbank.transaction.dto.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TransactionNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiErrorResponse notFound(TransactionNotFoundException ex, HttpServletRequest request) {
        return error(404, ex.getMessage(), request);
    }

    @ExceptionHandler({DuplicateTransactionException.class, InvalidTransactionException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiErrorResponse transactionConflict(RuntimeException ex, HttpServletRequest request) {
        return error(409, ex.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse validation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return error(400, message, request);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiErrorResponse generic(Exception ex, HttpServletRequest request) {
        return error(500, "Unexpected server error", request);
    }

    private ApiErrorResponse error(int status, String message, HttpServletRequest request) {
        return new ApiErrorResponse(LocalDateTime.now(), status, message, request.getRequestURI());
    }
}