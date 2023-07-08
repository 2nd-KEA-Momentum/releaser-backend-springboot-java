package com.momentum.releaser.domain.issue.api;

import com.momentum.releaser.domain.issue.application.IssueService;
import com.momentum.releaser.domain.issue.dto.IssueReqDto.IssueInfoReq;
import com.momentum.releaser.domain.issue.dto.IssueResDto;
import com.momentum.releaser.domain.issue.dto.IssueResDto.GetConnectionIssues;
import com.momentum.releaser.domain.issue.dto.IssueResDto.GetDoneIssues;
import com.momentum.releaser.domain.issue.dto.IssueResDto.GetIssue;
import com.momentum.releaser.domain.issue.dto.IssueResDto.GetIssuesList;
import com.momentum.releaser.global.config.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

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
    @PatchMapping("/{issueId}/member/{memberId}")
    public BaseResponse<String> updateIssue(@PathVariable @Min(1) Long issueId,
                                            @PathVariable @Min(1) Long memberId,
                                            @Valid @RequestBody IssueInfoReq updateReq) {
        return new BaseResponse<>(issueService.updateIssue(issueId, memberId, updateReq));
    }

    /**
     * 7.3 이슈 제거
     */

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
    public BaseResponse<List<GetDoneIssues>> getDoneIssues(@PathVariable @Min(1) Long projectId) {
        return new BaseResponse<>(issueService.getDoneIssues(projectId));

    }


    /**
     * 7.6 릴리즈 노트별 연결된 이슈 조회
     */
    @GetMapping("/project/{projectId}/release/{releaseId}")
    public BaseResponse<List<GetConnectionIssues>> getConnectRelease(@PathVariable @Min(1) Long projectId,
                                                               @PathVariable @Min(1) Long releaseId) {
        return new BaseResponse<>(issueService.getConnectRelese(projectId, releaseId));
    }

    /**
     * 7.7 이슈별 조회
     */
    @GetMapping("/{issueId}/member/{memberId}")
    public BaseResponse<GetIssue> getIssue(@PathVariable @Min(1) Long issueId,
                                           @PathVariable @Min(1) Long memberId) {
        return new BaseResponse<>(issueService.getIssue(issueId, memberId));
    }

    /**
     * 7.8 이슈 상태 변경
     */
    /**
     * 8.1 이슈 의견 추가
     */

    /**
     * 8.2 이슈 의견 삭제
     */

    /**
     * 8.3 이슈 의견 조회
     */
}
