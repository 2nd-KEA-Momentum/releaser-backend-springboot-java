package com.momentum.releaser.domain.release.dto;

import com.momentum.releaser.domain.release.dto.ReleaseDataDto.ReleasesDataDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class ReleaseResponseDto {

    /**
     * 5.1 프로젝트별 릴리즈 노트 목록 조회
     */
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ReleasesResponseDto {
        // 프로젝트 정보
        private Long projectId;
        private String title;
        private String team;
        private String img;

        // 릴리즈 노트 목록
        private List<ReleasesDataDto> releases;

        @Builder
        public ReleasesResponseDto(Long projectId, String title, String team, String img, List<ReleasesDataDto> releases) {
            this.projectId = projectId;
            this.title = title;
            this.team = team;
            this.img = img;
            this.releases = releases;
        }
    }
}
