package com.momentum.releaser.domain.issue.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class IssueResDto {
    /**
     * 이슈 조회
     */
    @Data
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class IssueInfoRes {

        private Long issueId;
        private String title;
        private String content;
        private Long memberId;
        private String memberName;
        private String memberImg;
        private String tag;
        private String releaseVersion;
        private char edit;
        private String lifeCycle;

        @Builder
        public IssueInfoRes(Long issueId, String title, String content, Long memberId, String memberName, String memberImg, String tag, String releaseVersion, char edit, String lifeCycle) {
            this.issueId = issueId;
            this.title = title;
            this.content = content;
            this.memberId = memberId;
            this.memberName = memberName;
            this.memberImg = memberImg;
            this.tag = tag;
            this.releaseVersion = releaseVersion;
            this.edit = edit;
            this.lifeCycle = lifeCycle;
        }
    }

}
