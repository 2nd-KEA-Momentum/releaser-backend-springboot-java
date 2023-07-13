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

        @NotBlank(message = "프로젝트명은 공백일 수 없습니다.")
        @Size(min = 1, max = 45, message = "프로젝트명은 1자 이상 45자 이하여야 합니다.")
        private String title;

        @NotBlank(message = "프로젝트 설명은 공백일 수 없습니다.")
        @Size(min = 1, max = 100, message = "프로젝트 설명은 1자 이상 100자 이하여야 합니다.")
        private String content;

        @NotBlank(message = "팀명은 공백일 수 없습니다.")
        private String team;

        @Builder
        public ProjectInfoReq(String title,String content, String team, MultipartFile img) {
            this.title = title;
            this.content = content;
            this.team = team;
        }
    }
}
