package com.momentum.releaser.domain.project.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ProjectMemberDataDto {

    /**
     * 릴리즈 노트 모달 하단에 프로젝트 멤버들의 프로필을 보여주는 부분
     * 5.2 릴리즈 노트 생성
     * 5.4 릴리즈 노트 조회
     */
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ProjectMembersDataDTO {
        private Long memberId;
        private String name;
        private String profileImg;

        @Builder
        public ProjectMembersDataDTO(Long memberId, String name, String profileImg) {
            this.memberId = memberId;
            this.name = name;
            this.profileImg = profileImg;
        }
    }



}
