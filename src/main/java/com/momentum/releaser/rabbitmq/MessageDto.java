package com.momentum.releaser.rabbitmq;

import java.time.LocalDateTime;
import java.util.Date;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MessageDto {

    /**
     * RabbitMQ 테스트를 위한 샘플 메시지 데이터
     */
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class SampleMessageDto {

        private String title;
        private String content;
    }

    /**
     * Releaser 프로젝트 알림
     */
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ReleaserMessageDto {

        private String projectName;
        private String message;
        private String type;
        private Date date;

        @Builder
        public ReleaserMessageDto(String projectName, String message, String type, Date date) {
            this.projectName = projectName;
            this.message = message;
            this.type = type;
            this.date = date;
        }
    }

    /**
     * 릴리즈 노트 유형 알림 메시지
     */
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ReleaseNoteMessageDto {
        private String project;
        private String message;
        private LocalDateTime date;
        private Long releaseNoteId;

        @Builder
        public ReleaseNoteMessageDto(String project, String message, LocalDateTime date, Long releaseNoteId) {
            this.project = project;
            this.message = message;
            this.date = date;
            this.releaseNoteId = releaseNoteId;
        }
    }

    /**
     * 이슈 유형 알림 메시지
     */
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class IssueMessageDto {
        private String project;
        private String message;
        private LocalDateTime date;
        private Long issueId;

        @Builder
        public IssueMessageDto(String project, String message, LocalDateTime date, Long issueId) {
            this.project = project;
            this.message = message;
            this.date = date;
            this.issueId = issueId;
        }
    }
}
