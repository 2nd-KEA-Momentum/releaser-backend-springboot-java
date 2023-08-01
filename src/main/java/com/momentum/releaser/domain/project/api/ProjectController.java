package com.momentum.releaser.domain.project.api;

import com.momentum.releaser.domain.project.application.ProjectService;
import com.momentum.releaser.domain.project.dto.ProjectReqDto.ProjectInfoReq;
import com.momentum.releaser.global.config.BaseResponse;
import com.momentum.releaser.global.jwt.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import java.io.IOException;

import static com.momentum.releaser.domain.project.dto.ProjectResDto.*;

@Slf4j
@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Validated
public class ProjectController {

    private final ProjectService projectService;

    /**
     * 3.1 프로젝트 생성
     */
    @PostMapping(value = "/project")
    public BaseResponse<ProjectInfoRes> createProject(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody @Valid ProjectInfoReq projectInfoReq) throws IOException {

        String email = userPrincipal.getEmail();
        return new BaseResponse<>(projectService.createProject(email, projectInfoReq));
    }


    /**
     * 3.2 프로젝트 수정
     */
    @PatchMapping(value = "/{projectId}")
    public BaseResponse<ProjectInfoRes> updateProject(
            @PathVariable @Min(value = 1, message = "프로젝트 식별 번호는 1 이상의 숫자여야 합니다.") Long projectId,
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody @Valid ProjectInfoReq projectInfoReq) throws IOException {
        String email = userPrincipal.getEmail();
        return new BaseResponse<>(projectService.updateProject(projectId, email, projectInfoReq));
    }

    /**
     * 3.3 프로젝트 삭제
     */
    @PostMapping("/{projectId}")
    public BaseResponse<String> deleteProject(
            @PathVariable @Min(value = 1, message = "프로젝트 식별 번호는 1 이상의 숫자여야 합니다.") Long projectId) {
        return new BaseResponse<>(projectService.deleteProject(projectId));
    }

    /**
     * 3.4 프로젝트 목록 조회
     */
    @GetMapping("/project")
    public BaseResponse<GetProjectRes> getProjects(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        String email = userPrincipal.getEmail();
        return new BaseResponse<>(projectService.getProjects(email));
    }

    /**
     * 10.1 프로젝트 내 통합검색
     */
    @GetMapping("/{projectId}/search")
    public BaseResponse<ProjectSearchRes> getSearch(
            @PathVariable @Min(value = 1, message = "프로젝트 식별 번호는 1 이상의 숫자여야 합니다.") Long projectId,
            @RequestParam String filterType,
            @RequestParam(required = false) String filterIssueGroup,
            @RequestParam(required = false) String filterReleaseGroup) {
        return new BaseResponse<>(projectService.getProjectSearch(projectId, filterTypeGroup, filterIssueGroup, filterReleaseGroup));
    }
}
