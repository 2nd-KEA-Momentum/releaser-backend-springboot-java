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
    public BaseResponse<ReleasesResponseDTO> releaseNoteList(@AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam @Min(value = 1, message = "프로젝트 식별 번호는 1 이상의 숫자여야 합니다.") Long projectId) {

        return new BaseResponse<>(releaseService.findReleaseNotes(userPrincipal.getEmail(), projectId));
    }

    /**
     * 5.2 릴리즈 노트 생성
     */
    @PostMapping(value = "/projects/{projectId}")
    public BaseResponse<ReleaseCreateAndUpdateResponseDTO> releaseNoteAdd(@AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable @Min(value = 1, message = "프로젝트 식별 번호는 1 이상의 숫자여야 합니다.") Long projectId,
            @RequestBody @Valid ReleaseCreateRequestDTO releaseCreateRequestDto) {

        return new BaseResponse<>(releaseService.addReleaseNote(userPrincipal.getEmail(), projectId, releaseCreateRequestDto));
    }

    /**
     * 5.3 릴리즈 노트 수정
     */
    @PatchMapping(value = "/{releaseId}")
    public BaseResponse<ReleaseCreateAndUpdateResponseDTO> releaseNoteSave(@AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable @Min(value = 1, message = "릴리즈 식별 번호는 1 이상의 숫자여야 합니다.") Long releaseId,
            @RequestBody @Valid ReleaseUpdateRequestDTO releaseUpdateRequestDto) {

        return new BaseResponse<>(releaseService.saveReleaseNote(userPrincipal.getEmail(), releaseId, releaseUpdateRequestDto));
    }

    /**
     * 5.4 릴리즈 노트 삭제
     */
    @PostMapping(value = "/{releaseId}")
    public BaseResponse<String> releaseNoteRemove(@AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable @Min(value = 1, message = "릴리즈 식별 번호는 1 이상의 숫자여야 합니다.") Long releaseId) {

        return new BaseResponse<>(releaseService.removeReleaseNote(userPrincipal.getEmail(), releaseId));
    }

    /**
     * 5.5 릴리즈 노트 조회
     */
    @GetMapping(value = "/{releaseId}")
    public BaseResponse<ReleaseInfoResponseDTO> releaseNoteDetails(@AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable @Min(value = 1, message = "릴리즈 식별 번호는 1 이상의 숫자여야 합니다.") Long releaseId) {

        return new BaseResponse<>(releaseService.findReleaseNote(userPrincipal.getEmail(), releaseId));
    }

    /**
     * 5.6 릴리즈 노트 배포 동의 여부 선택
     */
    @PostMapping(value = "/{releaseId}/approvals")
    public BaseResponse<List<ReleaseApprovalsResponseDTO>> releaseApprovalModify(@AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable @Min(value = 1, message = "릴리즈 식별 번호는 1 이상의 숫자여야 합니다.") Long releaseId,
            @RequestBody @Valid ReleaseApprovalRequestDTO releaseApprovalRequestDto) {

        return new BaseResponse<>(releaseService.modifyReleaseApproval(userPrincipal.getEmail(), releaseId, releaseApprovalRequestDto));
    }

    /**
     * 5.7 릴리즈 노트 그래프 좌표 추가
     */
    @PostMapping(value = "/coordinates")
    public BaseResponse<String> releaseCoordinateModify(@RequestBody @Valid ReleaseNoteCoordinateRequestDTO releaseNoteCoordinateRequestDto) {

        return new BaseResponse<>(releaseService.modifyReleaseCoordinate(releaseNoteCoordinateRequestDto));
    }

    /**
     * 6.1 릴리즈 노트 의견 추가
     */
    @PostMapping(value = "/{releaseId}/opinions")
    public BaseResponse<List<ReleaseOpinionsResponseDTO>> addReleaseOpinion(@AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable @Min(value = 1, message = "릴리즈 식별 번호는 1 이상의 숫자여야 합니다.") Long releaseId,
            @RequestBody @Valid ReleaseOpinionCreateRequestDTO releaseOpinionCreateRequestDto) {

        return new BaseResponse<>(releaseService.addReleaseOpinion(userPrincipal.getEmail(), releaseId, releaseOpinionCreateRequestDto));
    }

    /**
     * 6.2 릴리즈 노트 의견 삭제
     */
    @PostMapping("/opinions/{opinionId}")
    public BaseResponse<List<ReleaseOpinionsResponseDTO>> deleteReleaseOpinion(@AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable @Min(value = 1, message = "릴리즈 의견 식별 번호는 1 이상의 숫자여야 합니다.") Long opinionId) {

        return new BaseResponse<>(releaseService.deleteReleaseOpinion(userPrincipal.getEmail(), opinionId));
    }

    /**
     * 6.3 릴리즈 노트 의견 목록 조회
     */
    @GetMapping("/{releaseId}/opinions")
    public BaseResponse<List<ReleaseOpinionsResponseDTO>> getReleaseOpinions(@PathVariable @Min(value = 1, message = "릴리즈 식별 번호는 1 이상의 숫자여야 합니다.") Long releaseId) {

        return new BaseResponse<>(releaseService.getReleaseOpinions(releaseId));
    }

    /**
     * 9.1 프로젝트별 릴리즈 보고서 조회
     */
    @GetMapping("/project/{projectId}/docs")
    public BaseResponse<List<ReleaseDocsResponseDTO>> getReleaseDocs(@PathVariable @Min(value = 1, message = "프로젝트 식별 번호는 1 이상의 숫자여야 합니다.") Long projectId) {

        return new BaseResponse<>(releaseService.getReleaseDocs(projectId));
    }

    /**
     * 9.2 프로젝트별 릴리즈 보고서 수정
     */
    @PatchMapping("/project/{projectId}/docs")
    public BaseResponse<String> updateReleaseDocs(@PathVariable @Min(value = 1, message = "프로젝트 식별 번호는 1 이상의 숫자여야 합니다.") Long projectId,
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody @Valid List<UpdateReleaseDocsRequestDTO> updateReleaseDocsReq ) {
        String email = userPrincipal.getEmail();
        return new BaseResponse<>(releaseService.updateReleaseDocs(projectId, email, updateReleaseDocsReq));
    }
}
