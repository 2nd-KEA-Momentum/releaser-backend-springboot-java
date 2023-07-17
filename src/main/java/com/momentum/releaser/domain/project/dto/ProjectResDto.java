package com.momentum.releaser.domain.project.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.util.List;

public class ProjectResDto {

    /**
     * 프로젝트 정보 - 생성, 수정
     */
    @Data
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ProjectInfoRes {
        private Long projectId;

        @Builder
        public ProjectInfoRes(Long projectId) {
            this.projectId = projectId;
        }
    }

    /**
     * 프로젝트 조회
     */
    @Data
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class GetProjectRes {
        private List<GetProject> getCreateProjectList;
        private List<GetProject> getEnterProjectList;

        @Builder
        public GetProjectRes(List<GetProject> getCreateProjectList, List<GetProject> getEnterProjectList) {
            this.getCreateProjectList = getCreateProjectList;
            this.getEnterProjectList = getEnterProjectList;
        }
    }

    // 개별 프로젝트 조회
    @Data
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class GetProject {
        private Long projectId;
        private String title;
        private String content;
        private String team;
        private String img;

        @Builder
        public GetProject(Long projectId, String title, String content, String team, String img) {
            this.projectId = projectId;
            this.title = title;
            this.content = content;
            this.team = team;
            this.img = img;
        }
    }

    /**
     * 프로젝트 멤버 조회
     */
    @Data
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class GetMembersRes {
        private Long memberId;
        private Long userId;
        private String name;
        private String img;
        private char position;
        private char deleteYN;

        @QueryProjection
        @Builder
        public GetMembersRes(Long memberId, Long userId, String name, String img, char position) {
            this.memberId = memberId;
            this.userId = userId;
            this.name = name;
            this.img = img;
            this.position = position;
        }
    }

}
