package com.momentum.releaser.domain.issue.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

public class IssueDataDto {

    /**
     * 5.2 릴리즈 노트 생성
     * 5.5 릴리즈 노트 조회
     */
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ConnectedIssuesDataDto {
        private Long issueId;
        private String title;
        private String lifeCycle;
        private String tag;
        private char edit;
        private Date endDate;
        private Long memberId;
        private String memberName;
        private String memberProfileImg;

        @Builder
        public ConnectedIssuesDataDto(Long issueId, String title, String lifeCycle, String tag, Date endDate, char edit, Long memberId, String memberName, String memberProfileImg) {
            this.issueId = issueId;
            this.title = title;
            this.lifeCycle = lifeCycle;
            this.tag = tag;
            this.edit = edit;
            this.endDate = endDate;
            this.memberId = memberId;
            this.memberName = memberName;
            this.memberProfileImg = memberProfileImg;
        }
    }
}
