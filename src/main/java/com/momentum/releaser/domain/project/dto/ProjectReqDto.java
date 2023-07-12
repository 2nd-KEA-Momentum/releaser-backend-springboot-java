package com.momentum.releaser.domain.project.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ProjectReqDto {

    /**
     * 프로젝트 정보 - 생성, 수정
     */
    @Data
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ProjectInfoReq {

        @NotBlank
        @NotNull(message = "프로젝트명을 입력해주세요.")
        @Size(min = 1, max = 45)
        private String title;

        @NotBlank
        @NotNull(message = "프로젝트 설명을 입력해주세요.")
        @Size(min = 1, max = 100)
        private String content;

        @NotBlank
        @NotNull(message = "팀명을 입력해주세요.")
        private String team;

        @Builder
        public ProjectInfoReq(String title,String content, String team, MultipartFile img) {
            this.title = title;
            this.content = content;
            this.team = team;
        }
    }
}
