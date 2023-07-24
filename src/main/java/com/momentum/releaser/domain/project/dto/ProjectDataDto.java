package com.momentum.releaser.domain.project.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

public class ProjectDataDto {

    /**
     * 10.1 프로젝트 내 통합검색 - 릴리즈 정보
     */
    @Data
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class GetReleaseInfo {

        private Long releaseId;
        private String version;
        private String title;
        private Date deployDate;
        private Long pmId;
        private String pmName;
        private String pmImg;

        @Builder
        public GetReleaseInfo(Long releaseId, String version, String title, Date deployDate, Long pmId, String pmName, String pmImg) {
            this.releaseId = releaseId;
            this.version = version;
            this.title = title;
            this.deployDate = deployDate;
            this.pmId = pmId;
            this.pmName = pmName;
            this.pmImg = pmImg;
        }
    }

    /**
     * 10.1 프로젝트 내 통합검색 - 이슈 정보
     */
    @Data
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class GetIssueInfo {

        private Long issueId;
        private String title;
        private String tag;
        private String releaserVersion;
        private Date endDate;
        private Long manager;
        private String managerName;
        private String managerImg;

        @Builder
        public GetIssueInfo(Long issueId, String title, String tag, String releaserVersion, Date endDate, Long manager, String managerName, String managerImg) {
            this.issueId = issueId;
            this.title = title;
            this.tag = tag;
            this.releaserVersion = releaserVersion;
            this.endDate = endDate;
            this.manager = manager;
            this.managerName = managerName;
            this.managerImg = managerImg;
        }
    }


}
