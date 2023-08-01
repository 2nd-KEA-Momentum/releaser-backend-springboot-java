package com.momentum.releaser.domain.project.application;

import com.momentum.releaser.domain.issue.dao.IssueRepository;
import com.momentum.releaser.domain.issue.domain.Issue;
import com.momentum.releaser.domain.issue.domain.QIssue;
import com.momentum.releaser.domain.issue.domain.Tag;
import com.momentum.releaser.domain.project.dao.ProjectMemberRepository;
import com.momentum.releaser.domain.project.dao.ProjectRepository;
import com.momentum.releaser.domain.project.domain.Project;
import com.momentum.releaser.domain.project.domain.ProjectMember;
import com.momentum.releaser.domain.project.dto.ProjectReqDto.ProjectInfoReq;
import com.momentum.releaser.domain.project.dto.ProjectResDto.GetProject;
import com.momentum.releaser.domain.project.dto.ProjectResDto.GetProjectRes;
import com.momentum.releaser.domain.project.dto.ProjectResDto.ProjectInfoRes;
import com.momentum.releaser.domain.project.dto.ProjectResDto.ProjectSearchRes;
import com.momentum.releaser.domain.project.mapper.ProjectMapper;
import com.momentum.releaser.domain.release.dao.approval.ReleaseApprovalRepository;
import com.momentum.releaser.domain.release.dao.release.ReleaseRepository;
import com.momentum.releaser.domain.release.domain.QReleaseNote;
import com.momentum.releaser.domain.release.domain.ReleaseNote;
import com.momentum.releaser.domain.user.dao.UserRepository;
import com.momentum.releaser.domain.user.domain.User;
import com.momentum.releaser.global.config.aws.S3Upload;
import com.momentum.releaser.global.exception.CustomException;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.momentum.releaser.global.common.Base64.getImageUrlFromBase64;
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
    private final ReleaseRepository releaseRepository;
    private final ReleaseApprovalRepository releaseApprovalRepository;
    private final ModelMapper modelMapper;
    private final S3Upload s3Upload;

    /**
     * 3.1 프로젝트 생성
     */
    @Override
    @Transactional
    public ProjectInfoRes createProject(String email, ProjectInfoReq projectInfoReq) throws IOException {
        //Token UserInfo
        User user = findUserByEmail(email);

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
     */
    @Transactional
    @Override
    public ProjectInfoRes updateProject(Long projectId, String email, ProjectInfoReq projectInfoReq) throws IOException {
        Project project = getProjectById(projectId);
        User user = findUserByEmail(email);
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
     */
    @Override
    @Transactional
    public String deleteProject(Long projectId) {

        //project 정보
        Project project = getProjectById(projectId);

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
    public GetProjectRes getProjects(String email) {

        // 사용자 정보
        User user = findUserByEmail(email);

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

    /**
     * 10.2 프로젝트 내 통합검색
     */
    @Override
    @Transactional
    public ProjectSearchRes getProjectSearch(Long projectId,
                                                           String filterTypeGroup,
                                                           String filterIssueGroup,
                                                           String filterReleaseGroup) throws ParseException {

        //filterGroup 파싱하여 검색조건 만들기
        Predicate predicateRelease = buildPredicateFromFilters(filterTypeGroup, filterReleaseGroup);
        Predicate predicateIssue = buildPredicateFromFilters(filterTypeGroup, filterIssueGroup);

        // Query 실행
        Iterable<ReleaseNote> resultRelease = releaseRepository.findAll(predicateRelease);
        Iterable<Issue> resultIssue = issueRepository.findAll(predicateIssue);

        // Iterable을 List로 변환
        List<ReleaseNote> releaseNotes = StreamSupport.stream(resultRelease.spliterator(), false)
                .collect(Collectors.toList());

        List<Issue> issues = StreamSupport.stream(resultIssue.spliterator(), false)
                .collect(Collectors.toList());

        //mapper 사용해서 releaseRes 만들기
        //mapper 사용해서 issueRes 만들기


        //ProjectSearchRes 만들기
        return null;
    }

    private Predicate buildPredicateFromFilters(String filterTypeGroup, String filterGroup) throws ParseException {
        BooleanBuilder builder = new BooleanBuilder();

        if (filterTypeGroup.equals("issue")) {
            QIssue issue = QIssue.issue;

            Map<String, String> filterMap = parseFilterGroup(filterGroup);
            String startDateStr = filterMap.get("startDate");
            String endDateStr = filterMap.get("endDate");
            String manager = filterMap.get("manager");
            String version = filterMap.get("version");
            String tag = filterMap.get("tag");
            String title = filterMap.get("title");

            if (startDateStr != null && endDateStr != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date startDate = dateFormat.parse(startDateStr);
                Date endDate = dateFormat.parse(endDateStr);

                builder.and(issue.endDate.between(startDate, endDate));
            }
            if (manager != null) {
                builder.and(issue.member.memberId.eq(Long.valueOf(manager)));
            }
            if (version != null) {
                builder.and(issue.release.version.eq(version));
            }
            if (tag != null) {
                builder.and(issue.tag.eq(Tag.valueOf(tag)));
            }
            if (title != null) {
                builder.and(issue.title.containsIgnoreCase(title));
            }
        } else if ("release".equals(filterTypeGroup)) {
            QReleaseNote releaseNote = QReleaseNote.releaseNote;
        }
        return null;
    }

    private Map<String, String> parseFilterGroup(String filterGroup) {
        Map<String, String> filterMap = new HashMap<>();
        String[] filters = filterGroup.split(",");
        for (String filter : filters) {
            String[] keyValue = filter.split(":");
            if (keyValue.length == 2) {
                filterMap.put(keyValue[0], keyValue[1]);
            } else if (keyValue.length == 3 && "date".equals(keyValue[0])) {
                filterMap.put("startDate", keyValue[1]);
                filterMap.put("endDate", keyValue[2]);
            }
        }
        return filterMap;
    }



    // =================================================================================================================

    /**
     * 이메일로 User 가져오기
     */
    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new CustomException(NOT_EXISTS_USER));
    }

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
    private String uploadProjectImg(ProjectInfoReq projectInfoReq) throws IOException {
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
     */
    private String updateProjectImg(Project project, ProjectInfoReq projectInfoReq) throws IOException {
        deleteIfExistsProjectImg(project);
        return uploadProjectImg(projectInfoReq);
    }

    /**
     * 해당 프로젝트의 관리자 찾기
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
     */
    private Project getAndUpdateProject(Project project, ProjectInfoReq projectInfoReq, String url) {
        project.updateProject(projectInfoReq, url);
        return projectRepository.save(project);
    }

    /**
     * 프로젝트 생성
     */
    private Project createNewProject(ProjectInfoReq registerReq, String url) {
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
     */

    private String generateInviteLink() {
        // UUID를 이용하여 무작위의 초대 링크를 생성
        return UUID.randomUUID().toString();
    }

    /**
     * 프로젝트 멤버 추가
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
     * 프로젝트를 통해 프로젝트 관리자를 가져온다.
     */
    private ProjectMember getAdminMember(Project project) {
        List<ProjectMember> projectMembers = projectMemberRepository.findByProject(project);

        for (ProjectMember member : projectMembers) {
            if (member.getPosition() == 'L') {
                return member;
            }
        }
        throw new CustomException(NOT_EXISTS_ADMIN_MEMBER);

    }

    /**
     * 프로젝트 식별 번호를 통해 프로젝트 관리자의 사용자 엔티티를 가져온다.
     */
    private User getAdminUser(ProjectMember projectMember) {

        // 관리자(L) 멤버의 사용자 정보 조회
        return userRepository.findById(projectMember.getUser().getUserId()).orElseThrow(() -> new CustomException(NOT_EXISTS_USER));
    }
}
