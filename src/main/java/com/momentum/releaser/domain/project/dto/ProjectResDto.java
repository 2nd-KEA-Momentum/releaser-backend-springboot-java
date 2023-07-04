package com.momentum.releaser.domain.project.dto;

import lombok.*;

import javax.validation.constraints.NotNull;

public class ProjectResDto {

    /**
     * 프로젝트 생성
     */
    @Data
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ProjectInfoRes {
        private Long projectId;
        private String title;
        private String content;
        private String team;
        private String img;
        private Long memberId;
        private String admin;
        private String adminImg;

        @Builder
        public ProjectInfoRes(Long projectId, String title, String content, String team, String img, Long memberId, String admin, String adminImg) {
            this.projectId = projectId;
            this.title = title;
            this.content = content;
            this.team = team;
            this.img = img;
            this.memberId = memberId;
            this.admin = admin;
            this.adminImg = adminImg;
        }
    }


}
