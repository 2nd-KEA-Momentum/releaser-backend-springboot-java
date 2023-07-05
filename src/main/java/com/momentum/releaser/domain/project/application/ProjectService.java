package com.momentum.releaser.domain.project.application;

import com.momentum.releaser.domain.project.dto.ProjectReqDto.ProjectInfoReq;
import com.momentum.releaser.domain.project.dto.ProjectResDto.GetProjectRes;
import com.momentum.releaser.domain.project.dto.ProjectResDto.ProjectInfoRes;

public interface ProjectService {


    /**
     * 3.1 프로젝트 생성
     */
    ProjectInfoRes createProject(Long userId, ProjectInfoReq registerReq);

    /**
     * 3.2 프로젝트 수정
     */
    ProjectInfoRes updateProject(Long projectId, ProjectInfoReq updateReq);

    /**
     * 3.3 프로젝트 삭제
     */
    String deleteProject(Long projectId);

    /**
     * 3.4 프로젝트 조회
     */
    GetProjectRes getProjects(Long userId);
}
