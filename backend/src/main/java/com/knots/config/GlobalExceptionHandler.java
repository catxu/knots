package com.knots.config;

import com.oak.root.web.result.WebResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public WebResult handleValidationException(MethodArgumentNotValidException ex) {
        String errorMsg = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return WebResult.failResult("400", errorMsg);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public WebResult handleValidationException(IllegalArgumentException ex) {
        return WebResult.failResult("400", ex.getMessage());
    }
}
