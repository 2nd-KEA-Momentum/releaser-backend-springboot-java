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
        @NotBlank(message = "릴리즈 제목을 입력해 주세요.")
        @Size(min = 1, max = 45, message = "릴리즈 제목은 1자 이상 45자 이하여야 합니다.")
        private String title;

        @NotNull(message = "릴리즈 버전 타입을 선택해 주세요.")
        private String versionType;

        @Size(max = 1000, message = "릴리즈 설명은 1000자를 넘을 수 없습니다.")
        private String content;

        @Size(max = 100, message = "릴리즈 요약은 100자를 넘을 수 없습니다.")
        private String summary;

        private Date deployDate;

        List<Long> issues;

        @Builder
        public ReleaseCreateRequestDto(String title, String versionType, String content, String summary, Date deployDate, List<Long> issues) {
            this.title = title;
            this.versionType = versionType;
            this.content = content;
            this.summary = summary;
            this.deployDate = deployDate;
            this.issues = issues;
        }
    }

    /**
     * 5.3 릴리즈 노트 수정
     */
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ReleaseUpdateRequestDto {
        @NotNull(message = "릴리즈 제목을 입력해 주세요.")
        @NotBlank(message = "릴리즈 제목을 입력해 주세요.")
        @Size(min = 1, max = 45, message = "릴리즈 제목은 1자 이상 45자 이하여야 합니다.")
        private String title;

        @NotNull(message = "릴리즈 버전을 입력해 주세요.")
        @Pattern(regexp = "^(?!0)\\d+\\.\\d+\\.\\d+$", message = "릴리즈 버전 형식에 맞지 않습니다.")
        private String version;

        @Size(max = 1000, message = "릴리즈 설명은 1000자를 넘을 수 없습니다.")
        private String content;

        @Size(max = 100, message = "릴리즈 요약은 100자를 넘을 수 없습니다.")
        private String summary;

        @NotNull(message = "릴리즈 배포 날짜를 입력해 주세요.")
        private Date deployDate;

        List<Long> issues;

        @Builder
        public ReleaseUpdateRequestDto(String title, String version, String content, String summary, Date deployDate, List<Long> issues) {
            this.title = title;
            this.version = version;
            this.content = content;
            this.summary = summary;
            this.deployDate = deployDate;
            this.issues = issues;
        }
    }
}
