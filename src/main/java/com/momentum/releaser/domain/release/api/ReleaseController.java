package com.momentum.releaser.domain.release.api;

import com.momentum.releaser.domain.release.application.ReleaseServiceImpl;
import com.momentum.releaser.domain.release.dto.ReleaseRequestDto;
import com.momentum.releaser.domain.release.dto.ReleaseRequestDto.ReleaseCreateRequestDto;
import com.momentum.releaser.domain.release.dto.ReleaseRequestDto.ReleaseUpdateRequestDto;
import com.momentum.releaser.domain.release.dto.ReleaseResponseDto.ReleaseCreateResponseDto;
import com.momentum.releaser.domain.release.dto.ReleaseResponseDto.ReleasesResponseDto;
import com.momentum.releaser.global.config.BaseResponse;
import com.momentum.releaser.global.config.BaseResponseStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import static com.momentum.releaser.global.config.BaseResponseStatus.FAILED_TO_UPDATE_RELEASE_NOTE;
import static com.momentum.releaser.global.config.BaseResponseStatus.SUCCESS_TO_UPDATE_RELEASE_NOTE;

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
    public BaseResponse<ReleaseCreateResponseDto> createReleaseNote(
            @PathVariable @Min(1) Long projectId,
            @RequestBody @Valid ReleaseCreateRequestDto releaseCreateRequestDto) {

        return new BaseResponse<>(releaseService.createReleaseNote(projectId, releaseCreateRequestDto));
    }

    /**
     * 5.3 릴리즈 노트 수정
     */
    @PatchMapping(value = "/{releaseId}")
    public BaseResponse<String> updateReleaseNote(
            @PathVariable @Min(1) Long releaseId,
            @RequestBody @Valid ReleaseUpdateRequestDto releaseUpdateRequestDto) {

        if (releaseService.updateReleaseNote(releaseId, releaseUpdateRequestDto) == 1) {
            return new BaseResponse<>(SUCCESS_TO_UPDATE_RELEASE_NOTE);
        } else {
            return new BaseResponse<>(FAILED_TO_UPDATE_RELEASE_NOTE);
        }

    }
}
