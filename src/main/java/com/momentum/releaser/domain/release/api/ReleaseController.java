package com.momentum.releaser.domain.release.api;

import com.momentum.releaser.domain.release.application.ReleaseServiceImpl;
import com.momentum.releaser.domain.release.dto.ReleaseRequestDto.ReleaseCreateRequestDto;
import com.momentum.releaser.domain.release.dto.ReleaseResponseDto.ReleaseInfoResponseDto;
import com.momentum.releaser.domain.release.dto.ReleaseResponseDto.ReleasesResponseDto;
import com.momentum.releaser.global.config.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@Slf4j
@RestController
@RequestMapping("/api/releases")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Validated
public class ReleaseController {

    private final ReleaseServiceImpl releaseService;

    /**
     * 5.1 프로젝트별 릴리즈 노트 목록 조회
     */
    @GetMapping(value = "/projects")
    public BaseResponse<ReleasesResponseDto> getReleases(
            @RequestParam @Min(1) Long projectId) {

        return new BaseResponse<>(releaseService.getReleasesByProject(projectId));
    }

    /**
     * 5.2 릴리즈 노트 생성
     */
    @PostMapping(value = "/projects/{projectId}")
    public BaseResponse<ReleaseInfoResponseDto> createReleaseNote(
            @PathVariable @Min(1) Long projectId,
            @RequestBody @Valid ReleaseCreateRequestDto releaseCreateRequestDto) {

        return new BaseResponse<>(releaseService.createReleaseNote(projectId, releaseCreateRequestDto));
    }
}
