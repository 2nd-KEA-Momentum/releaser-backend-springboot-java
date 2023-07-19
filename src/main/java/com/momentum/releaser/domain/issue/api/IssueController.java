package com.momentum.releaser.domain.issue.api;

import com.momentum.releaser.domain.issue.application.IssueService;
import com.momentum.releaser.domain.issue.dto.IssueReqDto.IssueInfoReq;
import com.momentum.releaser.domain.issue.dto.IssueReqDto.UpdateLifeCycleReq;
import com.momentum.releaser.domain.issue.dto.IssueResDto.GetConnectionIssues;
import com.momentum.releaser.domain.issue.dto.IssueResDto.GetDoneIssues;
import com.momentum.releaser.domain.issue.dto.IssueResDto.GetIssuesList;
import com.momentum.releaser.domain.issue.dto.IssueResDto.OpinionInfoRes;
import com.momentum.releaser.global.config.BaseResponse;
import com.momentum.releaser.global.jwt.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

import static com.momentum.releaser.domain.issue.dto.IssueReqDto.*;
import static com.momentum.releaser.domain.issue.dto.IssueResDto.*;

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
    public BaseResponse<String> registerIssue(@PathVariable @Min(1) Long projectId,
                                              @Valid @RequestBody IssueInfoReq registerReq) {
        return new BaseResponse<>(issueService.registerIssue(projectId, registerReq));
    }

    /**
     * 7.2 이슈 수정
     */
    @PatchMapping("/issue/{issueId}")
    public BaseResponse<String> updateIssue(@PathVariable @Min(1) Long issueId,
                                            @AuthenticationPrincipal UserPrincipal userPrincipal,
                                            @Valid @RequestBody IssueInfoReq updateReq) {
        String email = userPrincipal.getEmail();
        return new BaseResponse<>(issueService.updateIssue(issueId, email, updateReq));
    }

    /**
     * 7.3 이슈 제거
     */
    @PostMapping("/{issueId}/delete")
    public BaseResponse<String> deleteIssue(@PathVariable @Min(1) Long issueId) {
        return new BaseResponse<>(issueService.deleteIssue(issueId));
    }

    /**
     * 7.4 프로젝트별 모든 이슈 조회
     */
    @GetMapping("/project/{projectId}")
    public BaseResponse<GetIssuesList> getIssues(@PathVariable @Min(1) Long projectId) {
        return new BaseResponse<>(issueService.getIssues(projectId));
    }

    /**
     * 7.5 프로젝트별 해결 & 미연결 이슈 조회
     */
    @GetMapping("/project/{projectId}/release")
    public BaseResponse<List<GetDoneIssues>> getDoneIssues(@PathVariable @Min(1) Long projectId,
                                                           @RequestParam
                                                           @Pattern(regexp = "(?i)^(DONE)$", message = "상태는 DONE 이어야 합니다.")
                                                           String status,
                                                           @RequestParam
                                                               @Pattern(regexp = "(?i)^(false)$", message = "연결 상태는 false 이어야 합니다.")
                                                               String connect) {
        return new BaseResponse<>(issueService.getDoneIssues(projectId, status));

    }


    /**
     * 7.6 릴리즈 노트별 연결된 이슈 조회
     */
    @GetMapping("/project/{projectId}/release/{releaseId}")
    public BaseResponse<List<GetConnectionIssues>> getConnectRelease(@PathVariable @Min(1) Long projectId,
                                                                     @PathVariable @Min(1) Long releaseId,
                                                                     @RequestParam(required = false, defaultValue = "false") boolean connect) {
        return new BaseResponse<>(issueService.getConnectRelease(projectId, releaseId));
    }

    /**
     * 7.7 이슈별 조회
     */
    @GetMapping("/{issueId}")
    public BaseResponse<GetIssue> getIssue(@PathVariable @Min(1) Long issueId,
                                           @AuthenticationPrincipal UserPrincipal userPrincipal) {
        String email = userPrincipal.getEmail();
        return new BaseResponse<>(issueService.getIssue(issueId, email));
    }

    /**
     * 7.8 이슈 상태 변경
     */
    @PatchMapping("/{issueId}")
    public BaseResponse<String> updateLifeCycle(@PathVariable @Min(1) Long issueId,
                                                @RequestParam(name = "status")
                                                @Pattern(regexp = "(?i)^(NOT_STARTED|IN_PROGRESS|DONE)$", message = "상태는 NOT_STARTED, IN_PROGRESS, DONE 중 하나여야 합니다.")
                                                String lifeCycle) {
        return new BaseResponse<>(issueService.updateLifeCycle(issueId, lifeCycle));
    }

    /**
     * 8.1 이슈 의견 추가
     */
    @PostMapping("/{issueId}/opinion")
    public BaseResponse<List<OpinionInfoRes>> registerOpinion(@PathVariable @Min(1) Long issueId,
                                                              @AuthenticationPrincipal UserPrincipal userPrincipal,
                                                        @Valid @RequestBody RegisterOpinionReq opinionReq) {
        String email = userPrincipal.getEmail();
        return new BaseResponse<>(issueService.registerOpinion(issueId, email, opinionReq));

    }

    /**
     * 8.2 이슈 의견 삭제
     */
    @PostMapping("/opinion/{opinionId}")
    public BaseResponse<String> deleteOpinion(@PathVariable @Min(1) Long opinionId,
                                              @AuthenticationPrincipal UserPrincipal userPrincipal) {
        String email = userPrincipal.getEmail();
        return new BaseResponse<>(issueService.deleteOpinion(opinionId, email));
    }


}
