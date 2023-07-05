package com.momentum.releaser.domain.release.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.util.Date;
import java.util.List;

public class ReleaseRequestDto {

    /**
     * 5.2 릴리즈 노트 생성
     * 릴리즈 제목, 릴리즈 버전, 릴리즈 설명, 릴리즈 요약, 연결된 이슈 식별 번호 목록
     */
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ReleaseCreateRequestDto {
        @NotBlank
        @Size(min = 1, max = 45)
        private String title;

        @Min(1)
        private Long memberId;

        @NotNull
        private String versionType;

        @Size(max = 1000)
        private String content;

        @Size(max = 100)
        private String summary;

        private Date deployDate;

        List<Long> issues;

        @Builder
        public ReleaseCreateRequestDto(String title, Long memberId, String versionType, String content, String summary, Date deployDate, List<Long> issues) {
            this.title = title;
            this.memberId = memberId;
            this.versionType = versionType;
            this.content = content;
            this.summary = summary;
            this.deployDate = deployDate;
            this.issues = issues;
        }
    }
}
