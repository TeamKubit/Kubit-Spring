package com.konkuk.kubit.exception;

import com.konkuk.kubit.domain.dto.ResultResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice // for exception handler : 특정 exception 발생시 여기서 처리하여 리턴할 수 있음
public class ExceptionManager {
    // AppException(custom)
    @ExceptionHandler(AppException.class)
    public ResponseEntity<?> appExceptionHandler(AppException e) {
        ResultResponse data = ResultResponse.builder()
                .result_code(e.getErrorCode().getHttpStatus().value())
                .result_msg(e.getMessage())
                .build();
        return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                .body(data);
    }
    //validation exceptions
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(
            MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        ResultResponse data = ResultResponse.builder()
                .result_code(400)
                .result_msg("missing parameters")
                .detail(errors)
                .build();
        return ResponseEntity.badRequest().body(data);
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handle400Exceptions(HttpMessageNotReadableException e){
        ResultResponse data = ResultResponse.builder()
                .result_code(400)
                .result_msg("HttpMessageNotReadableException")
                .detail(e.getMessage())
                .build();
        return ResponseEntity.badRequest().body(data);
    }
}
