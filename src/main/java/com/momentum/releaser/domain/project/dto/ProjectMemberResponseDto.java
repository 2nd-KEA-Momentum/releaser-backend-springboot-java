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
}
