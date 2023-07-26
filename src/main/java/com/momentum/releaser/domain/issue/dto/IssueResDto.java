package com.momentum.releaser.domain.issue.dto;

import com.momentum.releaser.domain.project.dto.ProjectResponseDto.GetMembersRes;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
        private Date endDate;
        private Long memberId;
        private String memberName;
        private String memberImg;
        private String tag;
        private String releaseVersion;
        private char edit;
        private String lifeCycle;
        private char deployYN;

        @Builder
        @QueryProjection
        public IssueInfoRes(Long issueId, Long issueNum, String title, String content, Date endDate, Long memberId, String memberName, String memberImg, String tag, String releaseVersion, char edit, String lifeCycle) {
            this.issueId = issueId;
            this.issueNum = issueNum;
            this.title = title;
            this.content = content;
            this.endDate = endDate;
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
        private Date endDate;
        private char edit;
        private Long memberId;
        private String memberName;
        private String memberImg;

        @Builder
        @QueryProjection
        public GetDoneIssues(Long issueId, Long issueNum, String title, String tag, Date endDate, char edit, Long memberId, String memberName, String memberImg) {
            this.issueId = issueId;
            this.issueNum = issueNum;
            this.title = title;
            this.tag = tag;
            this.endDate = endDate;
            this.edit = edit;
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
     * 이슈별 조회
     */
    @Data
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class GetIssue {
        private Long issueNum;
        private String title;
        private String content;
        private String tag;
        private Date endDate;
        private char edit;
        private Long manager; //담당자
        private char deployYN;
        private List<GetMembersRes> memberList;
        private List<OpinionInfoRes> opinionList;

        @Builder
        public GetIssue(Long issueNum, String title, String content, String tag, Date endDate, char edit, Long manager, List<GetMembersRes> memberList, List<OpinionInfoRes> opinionList) {
            this.issueNum = issueNum;
            this.title = title;
            this.content = content;
            this.tag = tag;
            this.endDate = endDate;
            this.edit = edit;
            this.manager = manager;
            this.memberList = memberList;
            this.opinionList = opinionList;
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
        private char deleteYN;
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
