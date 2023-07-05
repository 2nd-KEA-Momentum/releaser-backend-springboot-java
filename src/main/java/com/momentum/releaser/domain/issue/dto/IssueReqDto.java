package com.momentum.releaser.domain.issue.dto;

import com.momentum.releaser.domain.issue.domain.LifeCycle;
import com.momentum.releaser.domain.issue.domain.Tag;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

public class IssueReqDto {

    /**
     * 이슈 정보 - 생성, 수정
     */
//    @Data
//    @NoArgsConstructor(access = AccessLevel.PROTECTED)
//    public static class IssueInfoReq {
//
//        @NotBlank
//        @NotNull(message = "이슈명을 입력해주세요.")
//        private String title;
//
//        @NotBlank
//        @NotNull(message = "이슈 설명을 입력해주세요.")
//        private String content;
//
//        @NotBlank
//        @NotNull(message = "태그를 선택해주세요.")
//        private Tag tag;
//
//        private Date endDate;
//        private LifeCycle lifeCycle;
//        private List<RegisterOpinionReq> opinions;
//
//    }

    /**
     * 이슈 의견 정보 - 추가
     */
//    @Data
//    @NoArgsConstructor(access = AccessLevel.PROTECTED)
//    public static class RegisterOpinionReq {
//        private String opinion;
//    }

}
