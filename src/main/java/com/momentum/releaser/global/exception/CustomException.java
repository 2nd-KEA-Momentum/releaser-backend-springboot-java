package com.momentum.releaser.global.exception;


import com.momentum.releaser.global.config.BaseResponseStatus;
import lombok.Getter;

//@AllArgsConstructor
@Getter
public class CustomException extends RuntimeException {
    private final BaseResponseStatus exceptionStatus;
    private Long releaseId;

    public CustomException(BaseResponseStatus status) {
        super(status.getErrorMessage(null));
        this.exceptionStatus = status;
        this.releaseId = null;
    }

    public CustomException(BaseResponseStatus status, Long releaseId) {
        super(status.getErrorMessage(releaseId));
        this.exceptionStatus = status;
        this.releaseId = releaseId;
    }


}