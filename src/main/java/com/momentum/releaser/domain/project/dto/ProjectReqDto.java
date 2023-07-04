package com.momentum.releaser.domain.project.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class ProjectReqDto {

    /**
     * 프로젝트 정보 - 생성, 수정
     */
    @Data
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ProjectInfoReq {

        @NotBlank
        @NotNull(message = "프로젝트명을 입력해주세요.")
        private String title;

        @NotBlank
        @NotNull(message = "프로젝트 설명을 입력해주세요.")
        private String content;

        @NotBlank
        @NotNull(message = "팀명을 입력해주세요.")
        private String team;

        private String img;

        @Builder
        public ProjectInfoReq(String title,String content, String team, String img) {
            this.title = title;
            this.content = content;
            this.team = team;
            this.img = img;
        }
    }
}
