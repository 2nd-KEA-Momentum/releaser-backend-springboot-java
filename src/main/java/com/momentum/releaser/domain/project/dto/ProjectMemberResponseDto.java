package com.momentum.releaser.domain.project.dto;

import com.momentum.releaser.domain.project.dto.ProjectMemberDataDto.ProjectMemberInfoDTO;
import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.util.List;

public class ProjectMemberResponseDto {

    /**
     * 프로젝트 멤버 조회
     */
    @Data
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class MembersResponseDTO {
        private String link; //초대 링크
        private List<ProjectMemberInfoDTO> memberList;

        @QueryProjection
        @Builder
        public MembersResponseDTO(String link, List<ProjectMemberInfoDTO> memberList) {
            this.link = link;
            this.memberList = memberList;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class ProjectMemberPositionResponseDTO {
        private Long memberId;
        private char position;

        @Builder
        public ProjectMemberPositionResponseDTO(Long memberId, char position) {
            this.memberId = memberId;
            this.position = position;
        }
    }

    /**
     * 4.2 프로젝트 멤버 추가
     */
    @Getter
    @NoArgsConstructor
    public static class InviteProjectMemberResponseDTO {
        private Long projectId;
        private String projectName;

        @Builder
        public InviteProjectMemberResponseDTO(Long projectId, String projectName) {
            this.projectId = projectId;
            this.projectName = projectName;
        }
    }

}
