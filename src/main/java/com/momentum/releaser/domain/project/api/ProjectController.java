package com.momentum.releaser.domain.project.api;

import com.momentum.releaser.domain.project.application.ProjectService;
import com.momentum.releaser.domain.project.dto.ProjectReqDto.ProjectInfoReq;
import com.momentum.releaser.global.config.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.momentum.releaser.domain.project.dto.ProjectResDto.*;

@Slf4j
@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ProjectController {

    private final ProjectService projectService;

    /**
     * 3.1 프로젝트 생성
     */
    @PostMapping("/{userId}/project")
    public BaseResponse<ProjectInfoRes> createProject(@PathVariable Long userId, @Valid @RequestBody ProjectInfoReq registerReq) {
        return new BaseResponse<>(projectService.createProject(userId, registerReq));
    }

    /**
     * 3.2 프로젝트 수정
     */
//    @PatchMapping("/{projectId}")
//    public BaseResponse<ProjectInfoRes> updateProject(@PathVariable Long projectId, @Valid @RequestBody ProjectInfoReq registerReq) {
//
//    }
}
