package com.momentum.releaser.domain.project.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.*;

import java.util.Date;
import java.util.List;

public class ProjectRequestDto {

    /**
     * 프로젝트 정보
     */
    @Data
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ProjectInfoRequestDTO {

        @NotBlank(message = "프로젝트명은 공백일 수 없습니다.")
        @Size(min = 1, max = 45, message = "프로젝트명은 1자 이상 45자 이하여야 합니다.")
        private String title;

        @NotBlank(message = "프로젝트 설명은 공백일 수 없습니다.")
        @Size(min = 1, max = 100, message = "프로젝트 설명은 1자 이상 100자 이하여야 합니다.")
        private String content;

        @NotBlank(message = "팀명은 공백일 수 없습니다.")
        private String team;

        private String img;

        @Builder
        public ProjectInfoRequestDTO(String title,String content, String team, String img) {
            this.title = title;
            this.content = content;
            this.team = team;
            this.img = img == null ? "" : img;
        }
    }

    /**
     * requestParam - 이슈 그룹
     */
    @Data
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class FilterIssueRequestDTO {
        private Date startDate;
        private Date endDate;
        private Long managerId;
        private String startReleaseVersion;
        private String endReleaseVersion;

        @Pattern(regexp = "(?i)^(DEPRECATED|CHANGED|NEW|FEATURE|FIXED)$", message = "태그 타입은 DEPRECATED, CHANGED, NEW, FEATURE, FIXED 중 하나여야 합니다.")
        private String tag;

        private String issueTitle;
    }

    /**
     * requestParam - 릴리즈 그룹
     */
    @Data
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class FilterReleaseRequestDTO {
        private String startVersion;
        private String endVersion;
        private String releaseTitle;
    }

}
