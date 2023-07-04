package com.momentum.releaser.domain.project.api;

import com.momentum.releaser.domain.project.application.ProjectService;
import com.momentum.releaser.domain.project.dto.ProjectReqDto.ProjectInfoReq;
import com.momentum.releaser.global.config.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

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
    @PostMapping("/{userId}/project")
    public BaseResponse<ProjectInfoRes> createProject(
            @PathVariable @NotNull(message = "요청 데이터가 잘못되었습니다.") Long userId,
            @Valid @RequestBody ProjectInfoReq registerReq) {
        return new BaseResponse<>(projectService.createProject(userId, registerReq));
    }

    /**
     * 3.2 프로젝트 수정
     */
    @PatchMapping("/{projectId}")
    public BaseResponse<ProjectInfoRes> updateProject(
            @PathVariable @NotNull(message = "요청 데이터가 잘못되었습니다.") Long projectId,
            @Valid @RequestBody ProjectInfoReq updateReq) {
        return new BaseResponse<>(projectService.updateProject(projectId, updateReq));
    }

    /**
     * 3.3 프로젝트 삭제
     */


    /**
     * 3.4 프로젝트 목록 조회
     */
    @GetMapping("/{userId}")
    public BaseResponse<GetProjectRes> getProjects(@PathVariable @NotNull(message = "요청 데이터가 잘못되었습니다.") Long userId) {
        return new BaseResponse<>(projectService.getProjects(userId));
    }



}
