package com.sam.shallwego.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class BusinessException extends RuntimeException {
    private HttpStatus status;
    private String message;

    public BusinessException() {
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
        this.message = "서버가 커피를 마셔버림";
    }
}
