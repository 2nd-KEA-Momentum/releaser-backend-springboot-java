package com.momentum.releaser.domain.release.api;

import com.momentum.releaser.domain.release.application.ReleaseService;
import com.momentum.releaser.domain.release.dto.ReleaseRequestDto.*;
import com.momentum.releaser.domain.release.dto.ReleaseResponseDto.*;
import com.momentum.releaser.global.config.BaseResponse;
import com.momentum.releaser.global.jwt.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/releases")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Validated
public class ReleaseController {

    private final ReleaseService releaseService;

    /**
     * 5.1 프로젝트별 릴리즈 노트 목록 조회
     */
    @GetMapping(value = "/projects")
    public BaseResponse<ReleasesResponseDto> getReleases(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam @Min(value = 1, message = "프로젝트 식별 번호는 1 이상의 숫자여야 합니다.") Long projectId) {

        return new BaseResponse<>(releaseService.getReleasesByProject(userPrincipal.getEmail(), projectId));
    }

    /**
     * 5.2 릴리즈 노트 생성
     */
    @PostMapping(value = "/projects/{projectId}")
    public BaseResponse<ReleaseCreateAndUpdateResponseDto> createReleaseNote(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable @Min(value = 1, message = "프로젝트 식별 번호는 1 이상의 숫자여야 합니다.") Long projectId,
            @RequestBody @Valid ReleaseCreateRequestDto releaseCreateRequestDto) {

        return new BaseResponse<>(releaseService.createReleaseNote(userPrincipal.getEmail(), projectId, releaseCreateRequestDto));
    }

    /**
     * 5.3 릴리즈 노트 수정
     */
    @PatchMapping(value = "/{releaseId}")
    public BaseResponse<ReleaseCreateAndUpdateResponseDto> updateReleaseNote(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable @Min(value = 1, message = "릴리즈 식별 번호는 1 이상의 숫자여야 합니다.") Long releaseId,
            @RequestBody @Valid ReleaseUpdateRequestDto releaseUpdateRequestDto) {

        return new BaseResponse<>(releaseService.updateReleaseNote(userPrincipal.getEmail(), releaseId, releaseUpdateRequestDto));
    }

    /**
     * 5.4 릴리즈 노트 삭제
     */
    @PostMapping(value = "/{releaseId}")
    public BaseResponse<String> deleteReleaseNote(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable @Min(value = 1, message = "릴리즈 식별 번호는 1 이상의 숫자여야 합니다.") Long releaseId) {

        return new BaseResponse<>(releaseService.deleteReleaseNote(userPrincipal.getEmail(), releaseId));
    }

    /**
     * 5.5 릴리즈 노트 조회
     */
    @GetMapping(value = "/{releaseId}")
    public BaseResponse<ReleaseInfoResponseDto> getReleaseNote(
            @PathVariable @Min(value = 1, message = "릴리즈 식별 번호는 1 이상의 숫자여야 합니다.") Long releaseId) {

        return new BaseResponse<>(releaseService.getReleaseNoteInfo(releaseId));
    }

    /**
     * 5.6 릴리즈 노트 배포 동의 여부 선택 (멤버용)
     */
    @PostMapping(value = "/{releaseId}/approvals")
    public BaseResponse<List<ReleaseApprovalsResponseDto>> decideOnApprovalByMember(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable @Min(value = 1, message = "릴리즈 식별 번호는 1 이상의 숫자여야 합니다.") Long releaseId,
            @RequestBody @Valid ReleaseApprovalRequestDto releaseApprovalRequestDto) {

        return new BaseResponse<>(releaseService.decideOnApprovalByMember(userPrincipal.getEmail(), releaseId, releaseApprovalRequestDto));
    }

    /**
     * 5.7 릴리즈 노트 그래프 좌표 추가
     */
    @PostMapping(value = "/coordinates")
    public BaseResponse<String> updateReleaseNoteCoordinate(
            @RequestBody @Valid ReleaseNoteCoordinateRequestDto releaseNoteCoordinateRequestDto) {

        return new BaseResponse<>(releaseService.updateReleaseNoteCoordinate(releaseNoteCoordinateRequestDto));
    }

    /**
     * 6.1 릴리즈 노트 의견 추가
     */
    @PostMapping(value = "/{releaseId}/opinions")
    public BaseResponse<ReleaseOpinionCreateResponseDto> addReleaseOpinion(
            @PathVariable @Min(value = 1, message = "릴리즈 식별 번호는 1 이상의 숫자여야 합니다.") Long releaseId,
            @RequestBody @Valid ReleaseOpinionCreateRequestDto releaseOpinionCreateRequestDto) {

        return new BaseResponse<>(releaseService.addReleaseOpinion(releaseId, releaseOpinionCreateRequestDto));
    }

    /**
     * 6.2 릴리즈 노트 의견 삭제
     */
    @DeleteMapping("/opinions/{opinionId}")
    public BaseResponse<String> deleteReleaseOpinion(
            @PathVariable @Min(value = 1, message = "릴리즈 의견 식별 번호는 1 이상의 숫자여야 합니다.") Long opinionId) {

        return new BaseResponse<>(releaseService.deleteReleaseOpinion(opinionId));
    }

    /**
     * 6.3 릴리즈 노트 의견 목록 조회
     */
    @GetMapping("/{releaseId}/opinions")
    public BaseResponse<List<ReleaseOpinionsResponseDto>> getReleaseOpinions(
            @PathVariable @Min(value = 1, message = "릴리즈 식별 번호는 1 이상의 숫자여야 합니다.") Long releaseId) {

        return new BaseResponse<>(releaseService.getReleaseOpinions(releaseId));
    }

    /**
     * 9.1 프로젝트별 릴리즈 보고서 조회
     */
    @GetMapping("/project/{projectId}/docs")
    public BaseResponse<List<ReleaseDocsRes>> getReleaseDocs(
            @PathVariable @Min(value = 1, message = "프로젝트 식별 번호는 1 이상의 숫자여야 합니다.") Long projectId) {
        return new BaseResponse<>(releaseService.getReleaseDocs(projectId));

    }

    /**
     * 9.2 프로젝트별 릴리즈 보고서 수정
     */
    @PatchMapping("/project/{projectId}/docs")
    public BaseResponse<String> updateReleaseDocs(
            @PathVariable @Min(value = 1, message = "프로젝트 식별 번호는 1 이상의 숫자여야 합니다.") Long projectId,
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody @Valid List<UpdateReleaseDocsReq> updateReleaseDocsReq ) {
        String email = userPrincipal.getEmail();
        return new BaseResponse<>(releaseService.updateReleaseDocs(projectId, email, updateReleaseDocsReq));
    }
}
