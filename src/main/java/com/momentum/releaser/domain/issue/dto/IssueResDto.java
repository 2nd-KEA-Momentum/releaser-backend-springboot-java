package com.momentum.releaser.domain.issue.dto;

import com.momentum.releaser.domain.issue.domain.LifeCycle;
import com.momentum.releaser.domain.issue.domain.Tag;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class IssueResDto {
    /**
     * 이슈 상태 구분
     */
    @Data
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class GetIssuesList {
        private List<IssueInfoRes> getNotStartedList;
        private List<IssueInfoRes> getInProgressList;
        private List<IssueInfoRes> getDoneList;

        @Builder
        public GetIssuesList(List<IssueInfoRes> getNotStartedList, List<IssueInfoRes> getInProgressList, List<IssueInfoRes> getDoneList) {
            this.getNotStartedList = getNotStartedList;
            this.getInProgressList = getInProgressList;
            this.getDoneList = getDoneList;
        }
    }
    /**
     * 이슈 정보
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

        @QueryProjection
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
