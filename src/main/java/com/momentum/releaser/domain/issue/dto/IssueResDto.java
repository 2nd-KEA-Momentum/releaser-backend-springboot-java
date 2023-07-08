package com.momentum.releaser.domain.issue.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.momentum.releaser.domain.issue.domain.LifeCycle;
import com.momentum.releaser.domain.issue.domain.Tag;
import com.momentum.releaser.domain.project.dto.ProjectResDto;
import com.momentum.releaser.domain.project.dto.ProjectResDto.GetMembersRes;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class IssueResDto{
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
        private Long issueNum;
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
        @QueryProjection
        public IssueInfoRes(Long issueId, Long issueNum, String title, String content, Long memberId, String memberName, String memberImg, String tag, String releaseVersion, char edit, String lifeCycle) {
            this.issueId = issueId;
            this.issueNum = issueNum;
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

    /**
     * 프로젝트별 연결 가능한 이슈
     */
    @Data
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class GetDoneIssues {
        private Long issueId;
        private Long issueNum;
        private String title;
        private String tag;
        private Long memberId;
        private String memberName;
        private String memberImg;

        @Builder
        @QueryProjection
        public GetDoneIssues(Long issueId, Long issueNum, String title, String tag, Long memberId, String memberName, String memberImg) {
            this.issueId = issueId;
            this.issueNum = issueNum;
            this.title = title;
            this.tag = tag;
            this.memberId = memberId;
            this.memberName = memberName;
            this.memberImg = memberImg;
        }
    }

    /**
     * 프로젝트별 릴리즈와 연결된 이슈
     */
    @Data
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class GetConnectionIssues {
        private Long issueId;
        private Long issueNum;
        private String title;
        private String tag;
        private char edit;
        private Long memberId;
        private String memberName;
        private String memberImg;
        private String releaseVersion;

        @Builder
        @QueryProjection
        public GetConnectionIssues(Long issueId, Long issueNum, String title, String tag, char edit, Long memberId, String memberName, String memberImg, String releaseVersion) {
            this.issueId = issueId;
            this.issueNum = issueNum;
            this.title = title;
            this.tag = tag;
            this.edit = edit;
            this.memberId = memberId;
            this.memberName = memberName;
            this.memberImg = memberImg;
            this.releaseVersion = releaseVersion;
        }
    }

    /**
     * 이슈 의견 정보
     */
    @Data
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class OpinionInfoRes {
        private Long memberId;
        private String memberName;
        private String memberImg;
        private Long opinionId;
        private String opinion;

        @Builder
        @QueryProjection
        public OpinionInfoRes(Long memberId, String memberName, String memberImg, Long opinionId, String opinion) {
            this.memberId = memberId;
            this.memberName = memberName;
            this.memberImg = memberImg;
            this.opinionId = opinionId;
            this.opinion = opinion;
        }
    }


}
