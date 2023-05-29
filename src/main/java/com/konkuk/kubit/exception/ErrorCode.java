package com.konkuk.kubit.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    USERID_DUPLICATED(HttpStatus.CONFLICT, ""),
    USERID_NOTFOUND(HttpStatus.NOT_FOUND, ""),
    TRANSACTION_NOTFOUND(HttpStatus.NOT_FOUND, ""),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, ""),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, ""),
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, ""),
    LACK_OF_BALANCE(HttpStatus.PAYMENT_REQUIRED, ""),
    LACK_OF_QUANTITY(HttpStatus.BAD_REQUEST, ""),
    INVALID_MARKETCODE(HttpStatus.BAD_REQUEST, ""),
    REPOSITORY_EXCEPTION(HttpStatus.CONFLICT, ""),
    LOGIC_EXCEPTION(HttpStatus.CONFLICT, "");

    private HttpStatus httpStatus;
    private String message;
}
