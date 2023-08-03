package com.momentum.releaser.domain.project.application;

import static com.momentum.releaser.domain.project.dto.ProjectDataDto.*;
import static com.momentum.releaser.global.common.Base64.getImageUrlFromBase64;
import static com.momentum.releaser.global.common.CommonEnum.DEFAULT_PROJECT_IMG;
import static com.momentum.releaser.global.config.BaseResponseStatus.*;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.momentum.releaser.domain.issue.domain.Issue;
import com.momentum.releaser.domain.issue.domain.QIssue;
import com.momentum.releaser.domain.issue.domain.Tag;
import com.momentum.releaser.domain.project.dto.ProjectRequestDto;
import com.momentum.releaser.domain.project.dto.ProjectRequestDto.FilterIssueRequestDTO;
import com.momentum.releaser.domain.project.dto.ProjectRequestDto.FilterReleaseRequestDTO;
import com.momentum.releaser.domain.project.dto.ProjectResponseDto;
import com.momentum.releaser.domain.project.dto.ProjectResponseDto.ProjectSearchResponseDTO;
import com.momentum.releaser.domain.release.dao.release.ReleaseRepository;
import com.momentum.releaser.domain.release.domain.QReleaseNote;
import com.momentum.releaser.domain.release.domain.ReleaseNote;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.momentum.releaser.domain.issue.dao.IssueRepository;
import com.momentum.releaser.domain.project.dao.ProjectMemberRepository;
import com.momentum.releaser.domain.project.dao.ProjectRepository;
import com.momentum.releaser.domain.project.domain.Project;
import com.momentum.releaser.domain.project.domain.ProjectMember;
import com.momentum.releaser.domain.project.dto.ProjectRequestDto.ProjectInfoRequestDTO;
import com.momentum.releaser.domain.project.dto.ProjectResponseDto.GetProjectResponseDTO;
import com.momentum.releaser.domain.project.dto.ProjectResponseDto.ProjectInfoResponseDTO;
import com.momentum.releaser.domain.project.mapper.ProjectMapper;
import com.momentum.releaser.domain.release.dao.approval.ReleaseApprovalRepository;
import com.momentum.releaser.domain.user.dao.UserRepository;
import com.momentum.releaser.domain.user.domain.User;
import com.momentum.releaser.global.config.aws.S3Upload;
import com.momentum.releaser.global.exception.CustomException;

/**
 * 프로젝트와 관련된 기능을 제공하는 서비스 구현 클래스입니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final UserRepository userRepository;
    private final IssueRepository issueRepository;
    private final ReleaseRepository releaseRepository;
    private final ReleaseApprovalRepository releaseApprovalRepository;
    private final ModelMapper modelMapper;
    private final S3Upload s3Upload;

    /**
     * 3.1 프로젝트 생성
     *
     * @author chaeanna, seonwoo
     * @date 2023-07-04
     * @param email 사용자 이메일
     */
    @Override
    @Transactional
    public ProjectInfoResponseDTO addProject(String email, ProjectInfoRequestDTO projectInfoReq) throws IOException {
        //Token UserInfo
        User user = getUserByEmail(email);
        // S3 URL 생성한다.
        String url = uploadProjectImg(projectInfoReq);
        // 프로젝트 생성
        Project newProject = createNewProject(projectInfoReq, url);
        // 프로젝트 멤버 추가
        addProjectMember(newProject, user);
        // 프로젝트 응답 객체 생성
        return ProjectMapper.INSTANCE.toProjectInfoRes(newProject);
    }

    /**
     * 3.2 프로젝트 수정
     *
     * @author chaeanna, seonwoo
     * @date 2023-07-04
     * @param email 사용자 이메일
     */
    @Override
    @Transactional
    public ProjectInfoResponseDTO modifyProject(Long projectId, String email, ProjectInfoRequestDTO projectInfoReq) throws IOException {
        Project project = getProjectById(projectId);
        User user = getUserByEmail(email);
        ProjectMember leader = findLeaderForProject(project);

        // 접근 유저가 프로젝트 생성자인지 확인
        if (user.equals(leader.getUser())) {
            String url = updateProjectImg(project, projectInfoReq);
            Project updatedProject = getAndUpdateProject(project, projectInfoReq, url);
            return ProjectMapper.INSTANCE.toProjectInfoRes(updatedProject);
        } else {
            throw new CustomException(NOT_PROJECT_PM);
        }
    }

    /**
     * 3.3 프로젝트 삭제
     *
     * @author chaeanna
     * @date 2023-07-05
     */
    @Override
    @Transactional
    public String removeProject(Long projectId) {
        //project 정보
        Project project = getProjectById(projectId);

        projectRepository.deleteById(project.getProjectId());
        issueRepository.deleteByIssueNum();
        releaseApprovalRepository.deleteByReleaseApproval();

        return "프로젝트가 삭제되었습니다.";
    }

    /**
     * 3.4 프로젝트 조회
     *
     * @author chaeanna
     * @date 2023-07-04
     * @param email 사용자 이메일
     */
    @Override
    @Transactional
    public GetProjectResponseDTO findProjects(String email) {
        // 사용자 정보
        User user = getUserByEmail(email);

        // 프로젝트 멤버 정보
        List<ProjectMember> projectMemberList = projectMemberRepository.findByUser(user);
        List<GetProjectDataDTO> getCreateProjectList = new ArrayList<>();
        List<GetProjectDataDTO> getEnterProjectList = new ArrayList<>();

        for (ProjectMember projectMember : projectMemberList) {
            // 생성한 프로젝트 조회
            if (projectMember.getPosition() == 'L') {
                getCreateProjectList.add(mapToGetProject(projectMember.getProject()));
            } else { // 참가한 프로젝트 조회
                getEnterProjectList.add(mapToGetProject(projectMember.getProject()));
            }
        }

        return GetProjectResponseDTO.builder()
                .getCreateProjectList(getCreateProjectList)
                .getEnterProjectList(getEnterProjectList)
                .build();
    }

    /**
     * 10.2 프로젝트 내 통합검색
     */
    @Override
    @Transactional
    public ProjectSearchResponseDTO findProjectSearch(Long projectId, String filterType,
                                                      FilterIssueRequestDTO filterIssueGroup,
                                                      FilterReleaseRequestDTO filterReleaseGroup) {

        return null;

    }


    // =================================================================================================================

    /**
     * 이메일로 User 가져오기
     *
     * @author chaeanna
     * @date 2023-07-04
     * @param email 사용자 이메일
     * @return User 조회된 사용자 엔티티
     * @throws CustomException 사용자가 존재하지 않을 경우 발생하는 예외
     */
    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new CustomException(NOT_EXISTS_USER));
    }

    /**
     * 프로젝트 식별 번호를 이용하여 프로젝트 엔티티 가져오기
     *
     * @author seonwoo
     * @date 2023-07-04
     * @param projectId 프로젝트 식별 번호
     * @return Project 조회된 프로젝트 엔티티
     * @throws CustomException 프로젝트가 존재하지 않을 경우 발생하는 예외
     */
    private Project getProjectById(Long projectId) {
        return projectRepository.findById(projectId).orElseThrow(() -> new CustomException(NOT_EXISTS_PROJECT));
    }

    /**
     * 클라이언트로부터 받은 프로젝트 이미지를 S3에 업로드한다.
     *
     * @author seonwoo
     * @date 2023-07-04
     * @param projectInfoReq 프로젝트 생성 또는 수정 요청 객체
     * @return String 업로드된 이미지의 S3 URL
     * @throws IOException 이미지 업로드 중 오류가 발생한 경우 발생하는 예외
     */
    private String uploadProjectImg(ProjectInfoRequestDTO projectInfoReq) throws IOException {
        String img = projectInfoReq.getImg();

        if (img.isEmpty()) {
            // 만약 사용자로부터 받은 이미지 데이터가 없는 경우 기본 프로필로 대체한다.
            return DEFAULT_PROJECT_IMG.url();
        }

        // Base64로 인코딩된 이미지 파일을 파일 형태로 가져온다.
        File file = getImageUrlFromBase64(img);

        String url = s3Upload.upload(file, file.getName(), "projects");

        if (file.delete()) {
            return url;
        } else {
            throw new CustomException(FAILED_TO_CREATE_PROJECT);
        }
    }

    /**
     * 클라이언트로부터 받은 프로젝트 이미지로 수정한다.
     *
     * @author seonwoo
     * @date 2023-07-04
     * @param project 프로젝트 엔티티
     * @param projectInfoReq 프로젝트 수정 요청 객체
     * @return String 수정된 프로젝트 이미지의 S3 URL
     * @throws IOException 이미지 업로드 중 오류가 발생한 경우 발생하는 예외
     */
    private String updateProjectImg(Project project, ProjectInfoRequestDTO projectInfoReq) throws IOException {
        deleteIfExistsProjectImg(project);
        return uploadProjectImg(projectInfoReq);
    }

    /**
     * 해당 프로젝트의 관리자 찾기
     *
     * @author chaeanna
     * @date 2023-07-04
     * @param project 프로젝트 엔티티
     * @return ProjectMember 관리자(L) 포지션을 가진 프로젝트 멤버 엔티티
     */
    private ProjectMember findLeaderForProject(Project project) {
        List<ProjectMember> members = projectMemberRepository.findByProject(project);
        for (ProjectMember member : members) {

            if (member.getPosition() == 'L') {
                return member;
            }

        }
        return null;
    }

    /**
     * 프로젝트 이미지 값이 null이 아닌 경우 한 번 지운다.
     *
     * @author seonwoo
     * @date 2023-07-04
     * @param project 프로젝트 엔티티
     */
    private void deleteIfExistsProjectImg(Project project) {
        Project updatedProject = project;

        // 만약 "" 값이 들어가 있는 경우 null로 바꾼다.
        if (project.getImg().isEmpty() || project.getImg().isBlank()) {
            project.updateImg(null);
            updatedProject = projectRepository.save(project);
        }

        // 만약 프로젝트 이미지가 기본 이미지가 아닌 다른 파일이 들어가 있는 경우 파일을 삭제한다.
        if (!Objects.equals(updatedProject.getImg(), DEFAULT_PROJECT_IMG.url()) && updatedProject.getImg() != null) {
            s3Upload.delete(updatedProject.getImg().substring(55));
        }
    }

    /**
     * 프로필 이미지를 제외한 프로젝트 데이터를 업데이트한다.
     *
     * @author seonwoo
     * @date 2023-07-04
     * @param project 프로젝트 엔티티
     * @param projectInfoReq 프로젝트 수정 요청 객체
     * @param url 업로드된 프로젝트 이미지의 S3 URL
     * @return Project 업데이트된 프로젝트 엔티티
     */
    private Project getAndUpdateProject(Project project, ProjectInfoRequestDTO projectInfoReq, String url) {
        project.updateProject(projectInfoReq, url);
        return projectRepository.save(project);
    }

    /**
     * 프로젝트 생성
     *
     * @author seonwoo, chaeanna
     * @date 2023-07-04
     * @param registerReq 프로젝트 생성 요청 객체
     * @param url 업로드된 프로젝트 이미지의 S3 URL
     * @return Project 생성된 프로젝트 엔티티
     */
    private Project createNewProject(ProjectInfoRequestDTO registerReq, String url) {
        //초대 링크 생성
        String inviteLink = generateInviteLink();

        return projectRepository.save(Project.builder()
                .title(registerReq.getTitle())
                .content(registerReq.getContent())
                .link(inviteLink)
                .team(registerReq.getTeam())
                .img(url)
                .status('Y')
                .build());
    }

    /**
     * 초대 링크 생성
     *
     * @author chaeanna
     * @date 2023-07-04
     * @return String 생성된 초대 링크
     */
    private String generateInviteLink() {
        // UUID를 이용하여 무작위의 초대 링크를 생성
        return UUID.randomUUID().toString();
    }

    /**
     * 프로젝트 멤버 추가
     *
     * @author seonwoo
     * @date 2023-07-04
     * @param project 프로젝트 엔티티
     * @param user 사용자 엔티티
     */
    private void addProjectMember(Project project, User user) {
        ProjectMember projectMember = ProjectMember.builder()
                .position('L')
                .user(user)
                .project(project)
                .status('Y')
                .build();

        projectMemberRepository.save(projectMember);
    }

    /**
     * project mapper 사용
     *
     * @author chaeanna
     * @date 2023-07-04
     * @param project 프로젝트 엔티티
     * @return GetProjectDateDTO 변환된 프로젝트 DTO
     */
    private GetProjectDataDTO mapToGetProject(Project project) {
        return modelMapper.map(project, GetProjectDataDTO.class);
    }

}
