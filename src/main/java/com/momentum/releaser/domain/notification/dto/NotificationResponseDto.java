package com.momentum.releaser.domain.notification.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

public class NotificationResponseDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class NotificationListResponseDto {
        private String notificationId;
        private String type;
        private String projectTitle;
        private String projectImg;
        private String message;
        private Date date;
        private int isRead;

        @Builder
        public NotificationListResponseDto(String notificationId, String type, String projectTitle, String projectImg, String message, Date date, int isRead) {
            this.notificationId = notificationId;
            this.type = type;
            this.projectTitle = projectTitle;
            this.projectImg = projectImg;
            this.message = message;
            this.date = date;
            this.isRead = isRead;
        }

        public void updateIsRead(int isRead) {
            this.isRead = isRead;
        }
    }
}
