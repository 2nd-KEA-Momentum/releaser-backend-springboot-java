package com.momentum.releaser.domain.project.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.*;

public class ProjectRequestDto {

    /**
     * 프로젝트 정보
     */
    @Data
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ProjectInfoRequestDTO {

        @NotBlank(message = "프로젝트명은 공백일 수 없습니다.")
        @Size(min = 1, max = 45, message = "프로젝트명은 1자 이상 45자 이하여야 합니다.")
        private String title;

        @NotBlank(message = "프로젝트 설명은 공백일 수 없습니다.")
        @Size(min = 1, max = 100, message = "프로젝트 설명은 1자 이상 100자 이하여야 합니다.")
        private String content;

        @NotBlank(message = "팀명은 공백일 수 없습니다.")
        private String team;

        private String img;

        @Builder
        public ProjectInfoRequestDTO(String title,String content, String team, String img) {
            this.title = title;
            this.content = content;
            this.team = team;
            this.img = img;
        }
    }
}
