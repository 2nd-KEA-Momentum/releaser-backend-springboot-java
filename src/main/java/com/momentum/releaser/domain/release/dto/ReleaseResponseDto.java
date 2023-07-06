package com.momentum.releaser.domain.release.dto;

import com.momentum.releaser.domain.issue.dto.IssueDataDto.ConnectedIssuesDataDto;
import com.momentum.releaser.domain.project.dto.ProjectMemberDatoDto.ProjectMembersDataDto;
import com.momentum.releaser.domain.release.dto.ReleaseDataDto.ReleaseOpinionsDataDto;
import com.momentum.releaser.domain.release.dto.ReleaseDataDto.ReleasesDataDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

        // 릴리즈 노트 목록
        private List<ReleasesDataDto> releases;

        @Builder
        public ReleasesResponseDto(Long projectId, String title, String team, String img, List<ReleasesDataDto> releases) {
            this.projectId = projectId;
            this.title = title;
            this.team = team;
            this.img = img;
            this.releases = releases;
        }
    }

    /**
     * 5.2 릴리즈 노트 생성
     */
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ReleaseCreateResponseDto {
        private Long releaseId;

        @Builder
        public ReleaseCreateResponseDto(Long releaseId) {
            this.releaseId = releaseId;
        }
    }

    /**
     * 5.4 릴리즈 노트 조회
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
        private List<ProjectMembersDataDto> members;

        @Builder
        public ReleaseInfoResponseDto(Long releaseId, String title, String content, String summary, String version, Date deployDate, String deployStatus, List<ConnectedIssuesDataDto> issues, List<ReleaseOpinionsDataDto> opinions, List<ProjectMembersDataDto> members) {
            this.releaseId = releaseId;
            this.title = title;
            this.content = content;
            this.summary = summary;
            this.version = version;
            this.deployDate = deployDate;
            this.deployStatus = deployStatus;
            this.issues = issues;
            this.opinions = opinions;
            this.members = members;
        }
    }
}
