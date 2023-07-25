package com.momentum.releaser.global.exception;


import com.momentum.releaser.domain.project.dto.ProjectMemberResponseDto;
import com.momentum.releaser.domain.project.dto.ProjectMemberResponseDto.InviteProjectMemberRes;
import com.momentum.releaser.global.config.BaseResponseStatus;
import lombok.Getter;

//@AllArgsConstructor
@Getter
public class CustomException extends RuntimeException {
    private final BaseResponseStatus exceptionStatus;
    private Long releaseId;
    private InviteProjectMemberRes inviteProjectMemberRes;

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

    public CustomException(BaseResponseStatus status, InviteProjectMemberRes inviteProjectMemberRes) {
        super(status.getErrorMessageDto(inviteProjectMemberRes));
        this.exceptionStatus = status;
        this.inviteProjectMemberRes = inviteProjectMemberRes;
    }




}