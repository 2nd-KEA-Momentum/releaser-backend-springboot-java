package com.momentum.releaser.domain.project.application;

import com.momentum.releaser.domain.project.dto.ProjectReqDto.ProjectInfoReq;
import com.momentum.releaser.domain.project.dto.ProjectResDto;
import com.momentum.releaser.domain.project.dto.ProjectResDto.GetProjectRes;
import com.momentum.releaser.domain.project.dto.ProjectResDto.ProjectInfoRes;
import com.momentum.releaser.domain.project.dto.ProjectResDto.ProjectSearchRes;

import java.io.IOException;
import java.text.ParseException;

public interface ProjectService {


    /**
     * 3.1 프로젝트 생성
     */
    ProjectInfoRes createProject(String email, ProjectInfoReq projectInfoReq) throws IOException;

    /**
     * 3.2 프로젝트 수정
     */
    ProjectInfoRes updateProject(Long projectId, String email, ProjectInfoReq projectInfoReq) throws IOException;

    /**
     * 3.3 프로젝트 삭제
     */
    String deleteProject(Long projectId);

    /**
     * 3.4 프로젝트 조회
     */
    GetProjectRes getProjects(String email);

    /**
     * 10.1 프로젝트 내 통합검색
     */
    ProjectSearchRes getProjectSearch(Long projectId, String filterTypeGroup, String filterIssueGroup, String filterReleaseGroup) throws ParseException;
}
