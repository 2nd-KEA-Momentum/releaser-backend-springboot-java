package com.momentum.releaser.domain.issue.dto;

import com.momentum.releaser.domain.issue.domain.LifeCycle;
import com.momentum.releaser.domain.issue.domain.Tag;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

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
        private Tag tag;

        private Date endDate;
        private LifeCycle lifeCycle;
        private List<RegisterOpinionReq> opinions;


        @Builder
        public IssueInfoReq(String title, String content, Tag tag, Date endDate, LifeCycle lifeCycle, List<RegisterOpinionReq> opinions) {
            this.title = title;
            this.content = content;
            this.tag = tag;
            this.endDate = endDate;
            this.lifeCycle = lifeCycle;
            this.opinions = opinions;
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

}
