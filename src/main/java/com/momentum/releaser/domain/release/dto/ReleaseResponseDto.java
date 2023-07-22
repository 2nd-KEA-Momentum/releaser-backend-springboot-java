package com.momentum.releaser.domain.release.dto;

import com.momentum.releaser.domain.issue.dto.IssueDataDto.ConnectedIssuesDataDto;
import com.momentum.releaser.domain.project.dto.ProjectMemberResponseDto;
import com.momentum.releaser.domain.project.dto.ProjectMemberResponseDto.ProjectMemberPositionResponseDto;
import com.momentum.releaser.domain.release.domain.ReleaseEnum;
import com.momentum.releaser.domain.release.domain.ReleaseEnum.ReleaseDeployStatus;
import com.momentum.releaser.domain.release.dto.ReleaseDataDto.GetTags;
import com.momentum.releaser.domain.release.dto.ReleaseDataDto.ReleaseApprovalsDataDto;
import com.momentum.releaser.domain.release.dto.ReleaseDataDto.ReleaseOpinionsDataDto;
import com.momentum.releaser.domain.release.dto.ReleaseDataDto.ReleasesDataDto;
import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReleaseResponseDto {

    /**
     * 5.1 프로젝트별 릴리즈 노트 목록 조회
     */
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ReleasesResponseDto {
        // 프로젝트 정보
        private Long projectId;
        private String title;
        private String team;
        private String img;

        private ProjectMemberPositionResponseDto member;

        // 릴리즈 노트 목록
        private List<ReleasesDataDto> releases;

        @Builder
        public ReleasesResponseDto(Long projectId, String title, String team, String img, ProjectMemberPositionResponseDto member, List<ReleasesDataDto> releases) {
            this.projectId = projectId;
            this.title = title;
            this.team = team;
            this.img = img;
            this.member = member;
            this.releases = releases;
        }
    }

    /**
     * 5.2 릴리즈 노트 생성
     */
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ReleaseCreateAndUpdateResponseDto {
        private Long releaseId;
        private String version;
        private String summary;
        private Date deployDate;
        private ReleaseDeployStatus deployStatus;
        private Double coordX;
        private Double coordY;

        @Builder

        public ReleaseCreateAndUpdateResponseDto(Long releaseId, String version, String summary, Date deployDate, ReleaseDeployStatus deployStatus, Double coordX, Double coordY) {
            this.releaseId = releaseId;
            this.version = version;
            this.summary = summary;
            this.deployDate = deployDate;
            this.deployStatus = deployStatus;
            this.coordX = coordX;
            this.coordY = coordY;
        }
    }

    /**
     * 5.5 릴리즈 노트 조회
     */
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ReleaseInfoResponseDto {
        private Long releaseId;
        private String title;
        private String content;
        private String summary;
        private String version;
        private Date deployDate;
        private String deployStatus;
        private List<ConnectedIssuesDataDto> issues;
        private List<ReleaseOpinionsDataDto> opinions;
        private List<ReleaseApprovalsDataDto> approvals;

        @Builder
        public ReleaseInfoResponseDto(Long releaseId, String title, String content, String summary, String version, Date deployDate, String deployStatus, List<ConnectedIssuesDataDto> issues, List<ReleaseOpinionsDataDto> opinions, List<ReleaseApprovalsDataDto> approvals) {
            this.releaseId = releaseId;
            this.title = title;
            this.content = content;
            this.summary = summary;
            this.version = version;
            this.deployDate = deployDate;
            this.deployStatus = deployStatus;
            this.issues = issues;
            this.opinions = opinions;
            this.approvals = approvals;
        }
    }

    /**
     * 5.6 릴리즈 노트 배포 동의 여부 선택 (멤버용)
     */
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ReleaseApprovalsResponseDto {
        private Long memberId;
        private String memberName;
        private String memberProfileImg;
        private char approval;

        @Builder
        public ReleaseApprovalsResponseDto(Long memberId, String memberName, String memberProfileImg, char approval) {
            this.memberId = memberId;
            this.memberName = memberName;
            this.memberProfileImg = memberProfileImg;
            this.approval = approval;
        }
    }

    /**
     * 6.1 릴리즈 노트 의견 추가
     */
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ReleaseOpinionCreateResponseDto {
        private Long releaseOpinionId;

        @Builder
        public ReleaseOpinionCreateResponseDto(Long releaseOpinionId) {
            this.releaseOpinionId = releaseOpinionId;
        }
    }

    /**
     * 6.3 릴리즈 노트 의견 목록 조회
     */
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ReleaseOpinionsResponseDto {
        private Long releaseOpinionId;
        private String opinion;
        private Long memberId;
        private String memberName;
        private String memberProfileImg;

        @Builder
        public ReleaseOpinionsResponseDto(Long releaseOpinionId, String opinion, Long memberId, String memberName, String memberProfileImg) {
            this.releaseOpinionId = releaseOpinionId;
            this.opinion = opinion;
            this.memberId = memberId;
            this.memberName = memberName;
            this.memberProfileImg = memberProfileImg;
        }
    }

    /**
     * 9.1 프로젝트별 릴리즈 보고서 조회
     */
    @Data
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ReleaseDocsRes {
        private Long releaseId;
        private String releaseVersion;
        private String releaseTitle;
        private String releaseContent;
        private List<GetTags> tagsList = new ArrayList<>(); // 빈 리스트로 초기화

        @Builder
        public ReleaseDocsRes(Long releaseId, String releaseVersion, String releaseTitle, String releaseContent, List<GetTags> tagsList) {
            this.releaseId = releaseId;
            this.releaseVersion = releaseVersion;
            this.releaseTitle = releaseTitle;
            this.releaseContent = releaseContent;
            if (tagsList != null) {
                this.tagsList = tagsList;
            }
        }
    }

}
