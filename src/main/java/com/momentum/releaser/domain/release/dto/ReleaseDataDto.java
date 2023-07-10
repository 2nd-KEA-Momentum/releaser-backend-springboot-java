package com.momentum.releaser.domain.release.dto;

import com.momentum.releaser.domain.release.domain.ReleaseEnum.ReleaseDeployStatus;
import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Date;

public class ReleaseDataDto {

    /**
     * 5.1 프로젝트별 릴리즈 노트 목록 조회
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
     * 6.3 릴리즈 노트 의견 목록 조회
     */
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ReleaseOpinionsDataDto {
        private Long releaseOpinionId;
        private String opinion;
        private Long memberId;
        private String memberName;
        private String memberProfileImg;

        @Builder
        public ReleaseOpinionsDataDto(Long releaseOpinionId, String opinion, Long memberId, String memberName, String memberProfileImg) {
            this.releaseOpinionId = releaseOpinionId;
            this.opinion = opinion;
            this.memberId = memberId;
            this.memberName = memberName;
            this.memberProfileImg = memberProfileImg;
        }
    }

    /**
     * 5.5 릴리즈 노트 조회
     */
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ReleaseApprovalsDataDto {
        private Long memberId;
        private String memberName;
        private String memberProfileImg;
        private char approval;

        @Builder
        public ReleaseApprovalsDataDto(Long memberId, String memberName, String memberProfileImg, char approval) {
            this.memberId = memberId;
            this.memberName = memberName;
            this.memberProfileImg = memberProfileImg;
            this.approval = approval;
        }
    }

    /**
     * 5.7 릴리즈 노트 그래프 좌표 추가
     */
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class CoordinateDataDto {
        @NotNull(message = "릴리즈 식별 번호는 1 이상의 숫자여야 합니다.")
        @Min(value = 1, message = "릴리즈 식별 번호는 1 이상의 숫자여야 합니다.")
        private Long releaseId;

        @NotNull(message = "x 좌표를 입력해 주세요.")
        private Double coordX;

        @NotNull(message = "y 좌표를 입력해 주세요.")
        private Double coordY;

        @Builder
        public CoordinateDataDto(Long releaseId, Double coordX, Double coordY) {
            this.releaseId = releaseId;
            this.coordX = coordX;
            this.coordY = coordY;
        }
    }
}
