package com.momentum.releaser.domain.project.api;

import com.momentum.releaser.domain.project.application.ProjectService;
import com.momentum.releaser.domain.project.dto.ProjectRequestDto.ProjectInfoRequestDTO;
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

import static com.momentum.releaser.domain.project.dto.ProjectResponseDto.*;

/**
 * ProjectController는 프로젝트 관련된 API 엔드포인트를 처리하는 컨트롤러입니다.
 * 생성, 수정, 삭제, 조회 기능을 제공합니다.
 */
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
     *
     * @param userPrincipal 인증된 사용자 정보를 담고 있는 객체
     * @param projectInfoReq 프로젝트 생성 요청 객체
     * @return ProjectInfoResponseDTO 생성된 프로젝트 정보를 담은 응답 DTO
     * @throws IOException 이미지 업로드 중 오류가 발생한 경우 발생하는 예외
     */
    @PostMapping(value = "/project")
    public BaseResponse<ProjectInfoResponseDTO> projectAdd(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody @Valid ProjectInfoRequestDTO projectInfoReq) throws IOException {
        String email = userPrincipal.getEmail();
        return new BaseResponse<>(projectService.addProject(email, projectInfoReq));
    }

    /**
     * 3.2 프로젝트 수정
     *
     * @param projectId 프로젝트 식별 번호
     * @param userPrincipal 인증된 사용자 정보를 담고 있는 객체
     * @param projectInfoReq 프로젝트 수정 요청 객체
     * @return ProjectInfoResponseDTO 수정된 프로젝트 정보를 담은 응답 DTO
     * @throws IOException 이미지 업로드 중 오류가 발생한 경우 발생하는 예외
     */
    @PatchMapping(value = "/{projectId}")
    public BaseResponse<ProjectInfoResponseDTO> projectModify(
            @PathVariable @Min(value = 1, message = "프로젝트 식별 번호는 1 이상의 숫자여야 합니다.") Long projectId,
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody @Valid ProjectInfoRequestDTO projectInfoReq) throws IOException {
        String email = userPrincipal.getEmail();
        return new BaseResponse<>(projectService.modifyProject(projectId, email, projectInfoReq));
    }

    /**
     * 3.3 프로젝트 삭제
     *
     * @param projectId 프로젝트 식별 번호
     * @return BaseResponse<String> "프로젝트가 삭제되었습니다."
     */
    @PostMapping("/{projectId}")
    public BaseResponse<String> projectRemove(@PathVariable @Min(1) Long projectId) {
        return new BaseResponse<>(projectService.removeProject(projectId));
    }

    /**
     * 3.4 프로젝트 목록 조회
     *
     * @param userPrincipal 인증된 사용자 정보를 담고 있는 객체
     * @return GetProjectResponseDTO 조회된 프로젝트 목록 정보를 담은 응답 DTO
     */
    @GetMapping("/project")
    public BaseResponse<GetProjectResponseDTO>projectList(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        String email = userPrincipal.getEmail();
        return new BaseResponse<>(projectService.findProjects(email));
    }

    /**
     * 10.1 프로젝트 내 통합검색
     */
    @GetMapping("/{projectId}/search")
    public BaseResponse<ProjectSearchResponseDTO> getSearch(
            @PathVariable @Min(value = 1, message = "프로젝트 식별 번호는 1 이상의 숫자여야 합니다.") Long projectId,
            @RequestParam String filterType,
            @RequestParam(required = false) String filterIssueGroup,
            @RequestParam(required = false) String filterReleaseGroup) {
        return new BaseResponse<>(projectService.getProjectSearch(projectId, filterTypeGroup, filterIssueGroup, filterReleaseGroup));
    }
}
