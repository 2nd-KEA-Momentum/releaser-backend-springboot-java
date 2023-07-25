package com.momentum.releaser.domain.project.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ProjectMemberResponseDto {

    @Getter
    @NoArgsConstructor
    public static class ProjectMemberPositionResponseDto {
        private Long memberId;
        private char position;

        @Builder
        public ProjectMemberPositionResponseDto(Long memberId, char position) {
            this.memberId = memberId;
            this.position = position;
        }
    }

    /**
     * 4.2 프로젝트 멤버 추가
     */
    @Getter
    @NoArgsConstructor
    public static class InviteProjectMemberRes {
        private Long projectId;
        private String projectName;

        @Builder
        public InviteProjectMemberRes(Long projectId, String projectName) {
            this.projectId = projectId;
            this.projectName = projectName;
        }
    }

}
