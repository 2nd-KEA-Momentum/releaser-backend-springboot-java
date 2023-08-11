package com.momentum.releaser.domain.notification.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class NotificationRequestDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class NotificationApprovalRequestDto {
        Long releaseId;

        @Builder
        public NotificationApprovalRequestDto(Long releaseId) {
            this.releaseId = releaseId;
        }
    }
}
