package com.momentum.releaser.domain.release.dto;

import com.momentum.releaser.domain.release.domain.ReleaseEnum.ReleaseDeployStatus;
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
        private ReleaseDeployStatus deployStatus;
        private Double coordX;
        private Double coordY;

        @Builder
        public ReleasesDataDto(Long releaseId, String version, String summary, Date deployDate, ReleaseDeployStatus deployStatus, Double coordX, Double coordY) {
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
     * 6.4 릴리즈 노트 의견 목록 조회
     */
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ReleaseOpinionsDataDto {
        private Long opinionId;
        private String opinion;
        private Long memberId;
        private String memberName;
        private String memberProfileImg;

        @Builder
        public ReleaseOpinionsDataDto(Long opinionId, String opinion, Long memberId, String memberName, String memberProfileImg) {
            this.opinionId = opinionId;
            this.opinion = opinion;
            this.memberId = memberId;
            this.memberName = memberName;
            this.memberProfileImg = memberProfileImg;
        }
    }
}
