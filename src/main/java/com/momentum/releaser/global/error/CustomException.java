package com.momentum.releaser.global.error;


import com.momentum.releaser.global.config.BaseResponseStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CustomException extends RuntimeException {
    private final BaseResponseStatus exceptionStatus;
}