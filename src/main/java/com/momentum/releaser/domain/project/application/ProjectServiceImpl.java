package com.momentum.releaser.domain.project.application;

import com.momentum.releaser.domain.issue.dao.IssueNumRepository;
import com.momentum.releaser.domain.issue.dao.IssueRepository;
import com.momentum.releaser.domain.project.dao.ProjectMemberRepository;
import com.momentum.releaser.domain.project.dao.ProjectRepository;
import com.momentum.releaser.domain.project.domain.Project;
import com.momentum.releaser.domain.project.domain.ProjectMember;
import com.momentum.releaser.domain.project.dto.ProjectReqDto.ProjectInfoReq;
import com.momentum.releaser.domain.project.dto.ProjectResDto.GetProject;
import com.momentum.releaser.domain.project.dto.ProjectResDto.GetProjectRes;
import com.momentum.releaser.domain.project.dto.ProjectResDto.ProjectInfoRes;
import com.momentum.releaser.domain.project.mapper.ProjectMapper;
import com.momentum.releaser.domain.release.dao.approval.ReleaseApprovalRepository;
import com.momentum.releaser.domain.user.dao.UserRepository;
import com.momentum.releaser.domain.user.domain.User;
import com.momentum.releaser.global.error.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.momentum.releaser.global.config.BaseResponseStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final UserRepository userRepository;
    private final IssueRepository issueRepository;
    private final ReleaseApprovalRepository releaseApprovalRepository;
    private final ModelMapper modelMapper;

    /**
     * 3.1 프로젝트 생성
     */
    @Override
    @Transactional
    public ProjectInfoRes createProject(Long userId, ProjectInfoReq registerReq) {
        // 사용자 정보
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(NOT_EXISTS_USER));

        // 프로젝트 생성
        Project newProject = createNewProject(registerReq);

        // 프로젝트 멤버 추가
        ProjectMember projectMember = addProjectMember(newProject, user);

        // 프로젝트 응답 객체 생성
        ProjectInfoRes projectInfoRes = createProjectInfoResponse(newProject, user, projectMember);

        return projectInfoRes;
    }

    //email로 user 조회
    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new CustomException(NOT_EXISTS_USER));
    }

    // 프로젝트 생성
    private Project createNewProject(ProjectInfoReq registerReq) {
        return projectRepository.save(Project.builder()
                .title(registerReq.getTitle())
                .content(registerReq.getContent())
                .team(registerReq.getTeam())
                .img(registerReq.getImg())
                .status('Y')
                .build());
    }


    // 프로젝트 응답 객체 생성
    private ProjectInfoRes createProjectInfoResponse(Project project, User user, ProjectMember projectMember) {
        ProjectInfoRes projectInfoRes = modelMapper.map(project, ProjectInfoRes.class);
        projectInfoRes.setProjectId(project.getProjectId());
        projectInfoRes.setAdmin(user.getName());
        projectInfoRes.setMemberId(projectMember.getMemberId());
        projectInfoRes.setAdminImg(user.getImg());
        return projectInfoRes;
    }



    //프로젝트 멤버 추가
    private ProjectMember addProjectMember(Project project, User user) {
        ProjectMember projectMember = ProjectMember.builder()
                .position('L')
                .user(user)
                .project(project)
                .status('Y')
                .build();
        return projectMemberRepository.save(projectMember);
    }

    /**
     * 3.2 프로젝트 수정
     */
    @Override
    @Transactional
    public ProjectInfoRes updateProject(Long projectId, ProjectInfoReq updateReq) {

        // 프로젝트 정보
        Project project = findProject(projectId);

        // 프로젝트 업데이트
        project.updateProject(updateReq);
        Project updatedProject = projectRepository.save(project);

        // 프로젝트 멤버 정보
        List<ProjectMember> projectMembers = projectMemberRepository.findByProject(updatedProject);

        // 관리자(L) 멤버 조회
        ProjectMember adminMember = findAdminMember(projectMembers);

        // 관리자(L) 멤버의 사용자 정보 조회
        User adminUser = userRepository.findById(adminMember.getUser().getUserId())
                .orElseThrow(() -> new CustomException(NOT_EXISTS_USER));

        // 프로젝트 응답 객체 생성
        ProjectInfoRes projectInfoRes = createProjectInfoResponse(updatedProject, adminUser, adminMember);

        return projectInfoRes;
    }

    // 관리자(L) 멤버 조회
    private ProjectMember findAdminMember(List<ProjectMember> projectMembers) {
        for (ProjectMember member : projectMembers) {
            if (member.getPosition() == 'L') {
                return member;
            }
        }
        throw new CustomException(NOT_EXISTS_ADMIN_MEMBER);
    }

    //projectId로 Project 조회
    private Project findProject(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomException(NOT_EXISTS_PROJECT));
    }


    /**
     * 3.3 프로젝트 삭제
     */
    @Override
    @Transactional
    public String deleteProject(Long projectId) {
        //project 정보
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new CustomException(NOT_EXISTS_PROJECT));

        projectRepository.deleteById(project.getProjectId());
        issueRepository.deleteByIssueNum();
        releaseApprovalRepository.deleteByReleaseApproval();

        return "프로젝트가 삭제되었습니다.";
    }

    /**
     * 3.4 프로젝트 목록 조회
     */
    @Override
    @Transactional
    public GetProjectRes getProjects(Long userId) {
        // 사용자 정보
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(NOT_EXISTS_USER));

        // 프로젝트 멤버 정보
        List<ProjectMember> projectMemberList = projectMemberRepository.findByUser(user);
        List<GetProject> getCreateProjectList = new ArrayList<>();
        List<GetProject> getEnterProjectList = new ArrayList<>();

        for (ProjectMember projectMember : projectMemberList) {
            // 생성한 프로젝트 조회
            if (projectMember.getPosition() == 'L') {
                getCreateProjectList.add(mapToGetProject(projectMember.getProject()));
            }
            // 참가한 프로젝트 조회
            else {
                getEnterProjectList.add(mapToGetProject(projectMember.getProject()));
            }
        }

        GetProjectRes getProjectRes = GetProjectRes.builder()
                .getCreateProjectList(getCreateProjectList)
                .getEnterProjectList(getEnterProjectList)
                .build();

        return getProjectRes;
    }

    //project mapper 사용
    private GetProject mapToGetProject(Project project) {
        return modelMapper.map(project, GetProject.class);
    }

}
