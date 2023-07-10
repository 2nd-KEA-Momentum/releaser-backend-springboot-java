package com.momentum.releaser.domain.issue.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;


public class IssueReqDto {

    /**
     * 이슈 정보 - 생성, 수정
     */
    @Data
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class IssueInfoReq {

        @NotBlank
        @NotNull(message = "이슈명을 입력해주세요.")
        @Size(min = 1, max = 45)
        private String title;

        @NotBlank
        @NotNull(message = "이슈 설명을 입력해주세요.")
        @Size(min = 1, max = 500)
        private String content;

        @NotNull(message = "태그를 선택해주세요.")
        private String tag;

        private Date endDate;
        private Long memberId;

        @Builder
        public IssueInfoReq(String title, String content, String tag, Date endDate) {
            this.title = title;
            this.content = content;
            this.tag = tag;
            this.endDate = endDate;
        }
    }

    /**
     * 이슈 의견 정보 - 추가
     */
    @Data
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class RegisterOpinionReq {
        @NotBlank
        @NotNull(message = "의견을 입력해주세요.")
        @Size(min = 1, max = 300)
        private String opinion;

        @Builder
        public RegisterOpinionReq(String opinion) {
            this.opinion = opinion;
        }
    }

    /**
     * 이슈 상태 변경
     */
    @Data
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class UpdateLifeCycleReq {
        @NotBlank
        @NotNull(message = "이슈 상태를 입력해주세요.")
        private String lifeCycle;

        @Builder
        public UpdateLifeCycleReq(String lifeCycle) {
            this.lifeCycle = lifeCycle;
        }
    }



}
