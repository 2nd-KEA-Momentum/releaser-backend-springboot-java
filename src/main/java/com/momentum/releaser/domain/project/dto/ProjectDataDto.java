package com.momentum.releaser.domain.project.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.querydsl.core.annotations.QueryProjection;

public class ProjectDataDto {

    /**
     * 프로젝트 멤버 조회
     */
    @Data
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class GetMembers {
        private Long memberId;
        private Long userId;
        private String name;
        private String img;
        private char position;

        @QueryProjection
        @Builder
        public GetMembers(Long memberId, Long userId, String name, String img, char position) {
            this.memberId = memberId;
            this.userId = userId;
            this.name = name;
            this.img = img;
            this.position = position;
        }
    }

    /**
     * 개별 프로젝트 조회
     */
    @Data
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class GetProjectDateDTO {
        private Long projectId;
        private String title;
        private String content;
        private String team;
        private String img;

        @Builder
        public GetProjectDateDTO(Long projectId, String title, String content, String team, String img) {
            this.projectId = projectId;
            this.title = title;
            this.content = content;
            this.team = team;
            this.img = img;
        }
    }
}
