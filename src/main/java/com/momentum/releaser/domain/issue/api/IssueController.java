package com.momentum.releaser.domain.issue.api;

import com.momentum.releaser.domain.issue.application.IssueService;
import com.momentum.releaser.domain.issue.dto.IssueRequestDto.IssueInfoRequestDTO;
import com.momentum.releaser.domain.issue.dto.IssueResponseDto.ConnectionIssuesResponseDTO;
import com.momentum.releaser.domain.issue.dto.IssueResponseDto.DoneIssuesResponseDTO;
import com.momentum.releaser.domain.issue.dto.IssueResponseDto.OpinionInfoResponseDTO;
import com.momentum.releaser.global.config.BaseResponse;
import com.momentum.releaser.global.jwt.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.util.List;

import static com.momentum.releaser.domain.issue.dto.IssueRequestDto.*;
import static com.momentum.releaser.domain.issue.dto.IssueResponseDto.*;

@Slf4j
@RestController
@RequestMapping("/api/issues")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Validated
public class IssueController {

    private final IssueService issueService;

    /**
     * 7.1 이슈 생성
     */
    @PostMapping("/{projectId}")
    public BaseResponse<IssueIdResponseDTO> issueAdd(@PathVariable @Min(value = 1, message = "프로젝트 식별 번호는 1 이상의 숫자여야 합니다.") Long projectId,
                                              @Valid @RequestBody IssueInfoRequestDTO registerReq) {
        return new BaseResponse<>(issueService.addIssue(projectId, registerReq));
    }

    /**
     * 7.2 이슈 수정
     */
    @PatchMapping("/issue/{issueId}")
    public BaseResponse<String> issueModify(@PathVariable @Min(value = 1, message = "이슈 식별 번호는 1 이상의 숫자여야 합니다.") Long issueId,
                                            @AuthenticationPrincipal UserPrincipal userPrincipal,
                                            @Valid @RequestBody IssueInfoRequestDTO updateReq) {
        String email = userPrincipal.getEmail();
        return new BaseResponse<>(issueService.modifyIssue(issueId, email, updateReq));
    }

    /**
     * 7.3 이슈 제거
     */
    @PostMapping("/{issueId}/delete")
    public BaseResponse<String> issueRemove(@PathVariable @Min(value = 1, message = "이슈 식별 번호는 1 이상의 숫자여야 합니다.") Long issueId) {
        return new BaseResponse<>(issueService.removeIssue(issueId));
    }

    /**
     * 7.4 프로젝트별 모든 이슈 조회
     */
    @GetMapping("/project/{projectId}")
    public BaseResponse<AllIssueListResponseDTO> allIssueList(@PathVariable @Min(value = 1, message = "프로젝트 식별 번호는 1 이상의 숫자여야 합니다.") Long projectId) {
        return new BaseResponse<>(issueService.findAllIssues(projectId));
    }

    /**
     * 7.5 프로젝트별 해결 & 미연결 이슈 조회
     */
    @GetMapping("/project/{projectId}/release")
    public BaseResponse<List<DoneIssuesResponseDTO>> doneIssueList(@PathVariable @Min(value = 1, message = "프로젝트 식별 번호는 1 이상의 숫자여야 합니다.") Long projectId,
                                                            @RequestParam
                                                            @Pattern(regexp = "(?i)^(DONE)$", message = "상태는 DONE 이어야 합니다.") String status,
                                                            @RequestParam
                                                            @Pattern(regexp = "(?i)^(false)$", message = "연결 상태는 false 이어야 합니다.") String connect) {

        return new BaseResponse<>(issueService.findDoneIssues(projectId, status));
    }


    /**
     * 7.6 릴리즈 노트별 연결된 이슈 조회
     */
    @GetMapping("/project/{projectId}/release/{releaseId}")
    public BaseResponse<List<ConnectionIssuesResponseDTO>> connectIssueList(@PathVariable @Min(value = 1, message = "프로젝트 식별 번호는 1 이상의 숫자여야 합니다.") Long projectId,
                                                                     @PathVariable @Min(value = 1, message = "릴리즈 노트 식별 번호는 1 이상의 숫자여야 합니다.") Long releaseId,
                                                                     @RequestParam(required = false, defaultValue = "true") boolean connect) {
        return new BaseResponse<>(issueService.findConnectIssues(projectId, releaseId));
    }

    /**
     * 7.7 이슈별 조회
     */
    @GetMapping("/{issueId}")
    public BaseResponse<IssueDetailsDTO> issueDetails(@PathVariable @Min(value = 1, message = "이슈 식별 번호는 1 이상의 숫자여야 합니다.") Long issueId,
                                                @AuthenticationPrincipal UserPrincipal userPrincipal) {
        String email = userPrincipal.getEmail();
        return new BaseResponse<>(issueService.findIssue(issueId, email));
    }

    /**
     * 7.8 이슈 상태 변경
     */
    @PatchMapping("/{issueId}")
    public BaseResponse<String> issueLifeCycleModify(@PathVariable @Min(value = 1, message = "이슈 식별 번호는 1 이상의 숫자여야 합니다.") Long issueId,
                                                @RequestParam(name = "status")
                                                @Pattern(regexp = "(?i)^(NOT_STARTED|IN_PROGRESS|DONE)$", message = "상태는 NOT_STARTED, IN_PROGRESS, DONE 중 하나여야 합니다.")
                                                String lifeCycle) {
        return new BaseResponse<>(issueService.modifyIssueLifeCycle(issueId, lifeCycle));
    }

    /**
     * 8.1 이슈 의견 추가
     */
    @PostMapping("/{issueId}/opinion")
    public BaseResponse<List<OpinionInfoResponseDTO>> registerOpinion(@PathVariable @Min(value = 1, message = "이슈 식별 번호는 1 이상의 숫자여야 합니다.") Long issueId,
                                                              @AuthenticationPrincipal UserPrincipal userPrincipal,
                                                              @Valid @RequestBody RegisterOpinionReq opinionReq) {
        String email = userPrincipal.getEmail();
        return new BaseResponse<>(issueService.registerOpinion(issueId, email, opinionReq));

    }

    /**
     * 8.2 이슈 의견 삭제
     */
    @PostMapping("/opinion/{opinionId}")
    public BaseResponse<List<OpinionInfoResponseDTO>> deleteOpinion(@PathVariable @Min(value = 1, message = "이슈 의견 식별 번호는 1 이상의 숫자여야 합니다.") Long opinionId,
                                                            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        String email = userPrincipal.getEmail();
        return new BaseResponse<>(issueService.deleteOpinion(opinionId, email));
    }

}
