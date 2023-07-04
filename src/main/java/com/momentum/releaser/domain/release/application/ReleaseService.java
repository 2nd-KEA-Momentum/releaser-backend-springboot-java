package com.momentum.releaser.domain.release.application;

import com.momentum.releaser.domain.release.dto.ReleaseResponseDto.ReleasesResponseDto;

public interface ReleaseService {

    /**
     * 5.1 프로젝트별 릴리즈 노트 목록 조회
     */
    ReleasesResponseDto getReleasesByProject(Long projectId);
}
