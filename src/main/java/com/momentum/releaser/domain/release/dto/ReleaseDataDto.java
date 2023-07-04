package com.momentum.releaser.domain.release.dto;

import lombok.*;

import java.util.Date;

public class ReleaseDataDto {

    /**
     * 5.1 프로젝트별 릴리즈 노트 목록 조회
     * 릴리즈 식별 번호, 릴리즈 버전, 릴리즈 요약, 배포 날짜
     */
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ReleasesDataDto {
        private Long releaseId;
        private String version;
        private String summary;
        private Date deployDate;
        private String deployStatus;

        @Builder
        public ReleasesDataDto(Long releaseId, String version, String summary, Date deployDate, String deployStatus) {
            this.releaseId = releaseId;
            this.version = version;
            this.summary = summary;
            this.deployDate = deployDate;
            this.deployStatus = deployStatus;
        }
    }
}
