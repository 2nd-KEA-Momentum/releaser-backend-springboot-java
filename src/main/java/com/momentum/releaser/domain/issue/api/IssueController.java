package com.momentum.releaser.domain.issue.api;

import com.momentum.releaser.domain.issue.application.IssueService;
import com.momentum.releaser.domain.issue.dto.IssueReqDto;
import com.momentum.releaser.domain.issue.dto.IssueReqDto.IssueInfoReq;
import com.momentum.releaser.global.config.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

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
    public BaseResponse<String> registerIssue(
            @PathVariable @Min(1) Long projectId,
            @Valid @RequestBody IssueInfoReq registerReq) {
        return new BaseResponse<>(issueService.registerIssue(projectId, registerReq));
    }

    /**
     * 7.2 이슈 수정
     */
    @PatchMapping("/{issueId}")
    public BaseResponse<String> updateIssue(
            @PathVariable @Min(1) Long issueId,
            @Valid @RequestBody IssueInfoReq updateReq) {
        return new BaseResponse<>(issueService.updateIssue(issueId, updateReq));
    }

    /**
     * 7.3 이슈 제거
     */

    /**
     * 7.4 프로젝트별 모든 이슈 조회
     */

    /**
     * 7.5 프로젝트별 해결 & 미연결 이슈 조회
     */

    /**
     * 7.6 릴리즈 노트별 연결된 이슈 조회
     */

    /**
     * 7.7 이슈 검색
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
