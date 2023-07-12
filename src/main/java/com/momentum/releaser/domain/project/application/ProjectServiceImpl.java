package com.momentum.releaser.domain.project.application;

import com.momentum.releaser.domain.issue.dao.IssueRepository;
import com.momentum.releaser.domain.project.dao.ProjectMemberRepository;
import com.momentum.releaser.domain.project.dao.ProjectRepository;
import com.momentum.releaser.domain.project.domain.Project;
import com.momentum.releaser.domain.project.domain.ProjectMember;
import com.momentum.releaser.domain.project.dto.ProjectReqDto.ProjectInfoReq;
import com.momentum.releaser.domain.project.dto.ProjectResDto.GetProject;
import com.momentum.releaser.domain.project.dto.ProjectResDto.GetProjectRes;
import com.momentum.releaser.domain.project.dto.ProjectResDto.ProjectInfoRes;
import com.momentum.releaser.domain.release.dao.approval.ReleaseApprovalRepository;
import com.momentum.releaser.domain.user.dao.UserRepository;
import com.momentum.releaser.domain.user.domain.User;
import com.momentum.releaser.global.config.aws.S3Upload;
import com.momentum.releaser.global.error.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.momentum.releaser.global.common.CommonEnum.DEFAULT_PROJECT_IMG;
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
    private final S3Upload s3Upload;

    /**
     * 3.1 프로젝트 생성
     */
    @Override
    @Transactional
    public ProjectInfoRes createProject(Long userId, ProjectInfoReq registerReq, MultipartFile multipartFile) throws IOException {
        User user = getUserById(userId);

        // S3 URL 생성한다.
        String url = uploadProjectImg(multipartFile);

        // 프로젝트 생성
        Project newProject = createNewProject(registerReq, url);

        // 프로젝트 멤버 추가
        ProjectMember projectMember = addProjectMember(newProject, user);

        // 프로젝트 응답 객체 생성
        return createProjectInfoResponse(newProject, user, projectMember);
    }

    /**
     * 3.2 프로젝트 수정
     */
    @Transactional
    @Override
    public ProjectInfoRes updateProject(Long projectId, ProjectInfoReq updateReq, MultipartFile multipartFile) throws IOException {
        Project project = getProjectById(projectId);

        // S3 URL 생성
        String url = updateProjectImg(project, multipartFile);

        // 프로젝트 정보 가져오기 및 업데이트
        Project updatedProject = getAndUpdateProject(project, updateReq, url);

        // 프로젝트 멤버 정보
        ProjectMember adminMember = getAdminMember(updatedProject);
        User adminUser = getAdminUser(adminMember);

        // 프로젝트 응답 객체 생성
        return createProjectInfoResponse(updatedProject, adminUser, adminMember);
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

        return GetProjectRes.builder()
                .getCreateProjectList(getCreateProjectList)
                .getEnterProjectList(getEnterProjectList)
                .build();
    }

    // =================================================================================================================

    /**
     * 사용자 식별 번호를 이용하여 사용자 엔티티 가져오기
     */
    private User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new CustomException(NOT_EXISTS_USER));
    }

    /**
     * 프로젝트 식별 번호를 이용하여 프로젝트 엔티티 가져오기
     */
    private Project getProjectById(Long projectId) {
        return projectRepository.findById(projectId).orElseThrow(() -> new CustomException(NOT_EXISTS_PROJECT));
    }

    /**
     * 클라이언트로부터 받은 프로젝트 이미지를 S3에 업로드한다.
     */
    private String uploadProjectImg(MultipartFile multipartFile) throws IOException {
        // 만약 전달받은 이미지 파일이 없다면, 기본 이미지를 반환한다.
        return multipartFile.isEmpty() ? DEFAULT_PROJECT_IMG.url() : s3Upload.upload(multipartFile, "projects");
    }

    /**
     * 클라이언트로부터 받은 프로젝트 이미지로 수정한다.
     */
    private String updateProjectImg(Project project, MultipartFile multipartFile) throws IOException {
        deleteIfExistsProjectImg(project);
        return uploadProjectImg(multipartFile);
    }

    /**
     * 프로젝트 이미지 값이 null이 아닌 경우 한 번 지운다.
     */
    private void deleteIfExistsProjectImg(Project project) {
        // 만약 프로젝트 이미지가 기본 이미지가 아닌 다른 파일이 들어가 있는 경우 파일을 삭제한다.
        if (!Objects.equals(project.getImg(), DEFAULT_PROJECT_IMG.url()) && project.getImg() != null) {
            s3Upload.delete(project.getImg().substring(55));
        }
    }

    /**
     * 프로필 이미지를 제외한 프로젝트 데이터를 업데이트한다.
     */
    private Project getAndUpdateProject(Project project, ProjectInfoReq projectInfoReq, String url) {
        project.updateProject(projectInfoReq, url);
        return projectRepository.save(project);
    }

    /**
     * 프로젝트 생성
     */
    private Project createNewProject(ProjectInfoReq registerReq, String url) {
        return projectRepository.save(Project.builder()
                .title(registerReq.getTitle())
                .content(registerReq.getContent())
                .team(registerReq.getTeam())
                .img(url)
                .status('Y')
                .build());
    }

    /**
     * 프로젝트 응답 객체 생성
     */
    private ProjectInfoRes createProjectInfoResponse(Project project, User user, ProjectMember projectMember) {
        ProjectInfoRes projectInfoRes = modelMapper.map(project, ProjectInfoRes.class);
        projectInfoRes.setProjectId(project.getProjectId());
        projectInfoRes.setAdmin(user.getName());
        projectInfoRes.setMemberId(projectMember.getMemberId());
        projectInfoRes.setAdminImg(user.getImg());
        return projectInfoRes;
    }

    /**
     * 프로젝트 멤버 추가
     */
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
     * 관리자(L) 멤버 조회
     */
    private ProjectMember findAdminMember(List<ProjectMember> projectMembers) {
        for (ProjectMember member : projectMembers) {
            if (member.getPosition() == 'L') {
                return member;
            }
        }
        throw new CustomException(NOT_EXISTS_ADMIN_MEMBER);
    }

    /**
     * project mapper 사용
     */
    private GetProject mapToGetProject(Project project) {
        return modelMapper.map(project, GetProject.class);
    }

    /**
     * 프로젝트 식별 번호를 통해 프로젝트 관리자를 가져온다.
     */
    private ProjectMember getAdminMember(Project project) {
        List<ProjectMember> projectMembers = projectMemberRepository.findByProject(project);

        // 관리자(L) 멤버 조회
        return findAdminMember(projectMembers);
    }

    /**
     * 프로젝트 식별 번호를 통해 프로젝트 관리자의 사용자 엔티티를 가져온다.
     */
    private User getAdminUser(ProjectMember projectMember) {

        // 관리자(L) 멤버의 사용자 정보 조회
        return userRepository.findById(projectMember.getUser().getUserId()).orElseThrow(() -> new CustomException(NOT_EXISTS_USER));
    }
}
