package com.momentum.releaser.domain.release.dto;

import com.momentum.releaser.domain.release.domain.ReleaseEnum.ReleaseDeployStatus;
import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

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
        private char deleteYN;

        @QueryProjection
        @Builder
        public ReleaseOpinionsDataDto(Long releaseOpinionId, String opinion, Long memberId, String memberName, String memberProfileImg) {
            this.releaseOpinionId = releaseOpinionId;
            this.opinion = opinion;
            this.memberId = memberId;
            this.memberName = memberName;
            this.memberProfileImg = memberProfileImg;
        }

        /**
         * 해당 사용자가 릴리즈 노트 의견을 삭제할 수 있는지 아닌지를 알려주는 값을 업데이트한다.
         */
        public void updateDeleteYN(char deleteYN) {
            this.deleteYN = deleteYN;
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
        private char position;
        private char approval;

        @Builder
        public ReleaseApprovalsDataDto(Long memberId, String memberName, String memberProfileImg, char position, char approval) {
            this.memberId = memberId;
            this.memberName = memberName;
            this.memberProfileImg = memberProfileImg;
            this.position = position;
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

    /**
     * 9.1 프로젝트별 릴리즈 보고서 조회
     */
    @Data
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class GetTags {
        private String tag;
        private List<GetIssueTitle> titleList;

        @Builder
        public GetTags(String tag, List<GetIssueTitle> titleList) {
            this.tag = tag;
            this.titleList = titleList;
        }
    }

    /**
     * 9.1 프로젝트별 릴리즈 보고서 조회
     */
    @Data
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class GetIssueTitle {
        private Long issueId;
        private String title;
        private String summary;

        @Builder
        public GetIssueTitle(Long issueId, String title, String summary) {
            this.issueId = issueId;
            this.title = title;
            this.summary = summary;
        }
    }
}
