package com.momentum.releaser.domain.release.dto;

import lombok.AccessLevel;
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
        @NotBlank
        private String version;

        @Size(max = 1000)
        private String content;

        @Size(max = 100)
        private String summary;

        @FutureOrPresent
        private Date deployDate;

        List<Long> issues;
    }
}
