package com.momentum.releaser.domain.issue.dto;

import java.util.Date;
import java.util.List;

import com.momentum.releaser.domain.project.dto.ProjectDataDto;
import lombok.*;

public class IssueDataDto {

    /**
     * 5.2 릴리즈 노트 생성
     * 5.5 릴리즈 노트 조회
     */
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ConnectedIssuesDataDTO {
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
        public ConnectedIssuesDataDTO(Long issueId, String title, String lifeCycle, String tag, Date endDate, char edit, Long memberId, String memberName, String memberProfileImg) {
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

    /**
     * 이슈별 조회 - 이슈에 필요한 정보
     */
    @Data
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class IssueDetailsDataDTO {
        private Long issueNum;
        private String title;
        private String content;
        private String tag;
        private Date endDate;
        private char edit;
        private Long manager; //담당자
        private char deployYN;
        private List<ProjectDataDto.GetMembers> memberList;
        private List<IssueResponseDto.OpinionInfoResponseDTO> opinionList;

        @Builder
        public IssueDetailsDataDTO(Long issueNum, String title, String content, String tag, Date endDate, char edit, Long manager, char deployYN, List<ProjectDataDto.GetMembers> memberList, List<IssueResponseDto.OpinionInfoResponseDTO> opinionList) {
            this.issueNum = issueNum;
            this.title = title;
            this.content = content;
            this.tag = tag;
            this.endDate = endDate;
            this.edit = edit;
            this.manager = manager;
            this.deployYN = deployYN;
            this.memberList = memberList;
            this.opinionList = opinionList;
        }
    }
}
