package com.momentum.releaser.domain.issue.application;

import static com.momentum.releaser.domain.issue.dto.IssueResponseDto.*;
import static com.momentum.releaser.global.config.BaseResponseStatus.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.momentum.releaser.domain.issue.dto.IssueDataDto.IssueDetailsDataDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;

import com.momentum.releaser.domain.issue.dao.IssueNumRepository;
import com.momentum.releaser.domain.issue.dao.IssueOpinionRepository;
import com.momentum.releaser.domain.issue.dao.IssueRepository;
import com.momentum.releaser.domain.issue.domain.*;
import com.momentum.releaser.domain.issue.dto.IssueRequestDto.IssueInfoRequestDTO;
import com.momentum.releaser.domain.issue.dto.IssueRequestDto.RegisterOpinionRequestDTO;
import com.momentum.releaser.domain.issue.mapper.IssueMapper;
import com.momentum.releaser.domain.project.dao.ProjectMemberRepository;
import com.momentum.releaser.domain.project.dao.ProjectRepository;
import com.momentum.releaser.domain.project.domain.Project;
import com.momentum.releaser.domain.project.domain.ProjectMember;
import com.momentum.releaser.domain.project.dto.ProjectDataDto.GetMembersDataDTO;
import com.momentum.releaser.domain.release.dao.release.ReleaseRepository;
import com.momentum.releaser.domain.release.domain.ReleaseNote;
import com.momentum.releaser.domain.user.dao.UserRepository;
import com.momentum.releaser.domain.user.domain.User;
import com.momentum.releaser.global.config.BaseResponseStatus;
import com.momentum.releaser.global.exception.CustomException;

/**
 * 이슈 관련된 기능을 제공하는 서비스 구현 클래스입니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IssueServiceImpl implements IssueService {

    private final IssueRepository issueRepository;
    private final IssueOpinionRepository issueOpinionRepository;
    private final IssueNumRepository issueNumRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final UserRepository userRepository;
    private final ReleaseRepository releaseRepository;
    private final ModelMapper modelMapper;

    /**
     * 7.1 이슈 생성
     *
     * @author chaeanna
     * @date 2023-07-27
     */
    @Override
    @Transactional
    public IssueIdResponseDTO addIssue(Long projectId, IssueInfoRequestDTO createReq) {
        ProjectMember projectMember = null;

        // 담당자 memberId가 null이 아닌 경우 프로젝트 멤버 조회
        if (createReq.getMemberId() != null) {
            projectMember = getProjectMemberById(createReq.getMemberId());
        }

        Project project = getProjectById(projectId);

        // 이슈를 생성하고 이슈 번호를 할당하여 저장
        Issue newIssue = createIssueNumAndSaveIssue(createReq, project, projectMember);

        return IssueIdResponseDTO.builder()
                .issueId(newIssue.getIssueId())
                .build();
    }

    /**
     * 7.2 이슈 수정
     *
     * @author chaeanna
     * @date 2023-07-05
     * @param email 사용자 이메일
     */
    @Override
    @Transactional
    public IssueModifyResponseDTO modifyIssue(Long issueId, String email, IssueInfoRequestDTO updateReq) {
        // 이슈 정보 조회
        Issue issue = getIssueById(issueId);

        // Token UserInfo
        User user = getUserByEmail(email);
        ProjectMember projectMember = getProjectMemberByUserAndProject(user, issue.getProject());

        // 접근한 유저가 멤버일 경우 edit 상태 변경
        char edit = decideEditStatus(projectMember.getMemberId());

        ProjectMember manager = null;
        // 담당자 memberId가 null이 아닌 경우 프로젝트 멤버 조회
        if (updateReq.getMemberId() != null) {
            manager = getProjectMemberById(updateReq.getMemberId());
        }

        // 이슈 업데이트
        issue.updateIssue(updateReq, edit, manager);
        issueRepository.save(issue);

        return IssueMapper.INSTANCE.toIssueModifyResponseDTO(projectMember);
    }

    /**
     * 7.3 이슈 제거
     *
     * @author chaeanna
     * @date 2023-07-09
     */
    @Override
    @Transactional
    public String removeIssue(Long issueId) {
        // 이슈 정보 조회
        Issue issue = getIssueById(issueId);

        // issue와 연결된 릴리즈가 있으면 삭제가 불가능, 예외 발생
        if (issue.getRelease() != null) {
            Long releaseId = issue.getRelease().getReleaseId();
            throw new CustomException(CONNECTED_RELEASE_EXISTS, releaseId);
        }

        // 이슈 번호와 이슈 삭제
        issueNumRepository.deleteById(issue.getIssueNum().getIssueNumId());
        issueRepository.deleteById(issue.getIssueId());

        return "이슈가 삭제되었습니다.";
    }

    /**
     * 7.4 프로젝트별 모든 이슈 조회
     *
     * @author chaeanna
     * @date 2023-07-08
     */
    @Override
    @Transactional
    public AllIssueListResponseDTO findAllIssues(Long projectId) {
        // 프로젝트 정보 조회
        Project findProject = getProjectById(projectId);

        // 해당 프로젝트에 속하는 모든 이슈 정보
        List<IssueInfoResponseDTO> getAllIssue = issueRepository.getIssues(findProject);

        // 각 상태별로 이슈를 분류
        List<IssueInfoResponseDTO> notStartedList = filterAndSetDeployStatus(getAllIssue, "NOT_STARTED");
        List<IssueInfoResponseDTO> inProgressList = filterAndSetDeployStatus(getAllIssue, "IN_PROGRESS");
        List<IssueInfoResponseDTO> doneList = filterAndSetDeployStatus(getAllIssue, "DONE");

        // 분류된 리스트들을 담아 반환
        return AllIssueListResponseDTO.builder()
                .getNotStartedList(notStartedList)
                .getInProgressList(inProgressList)
                .getDoneList(doneList)
                .build();
    }

    /**
     * 7.5 프로젝트별 해결 & 미연결 이슈 조회
     *
     * @author chaeanna
     * @date 2023-07-08
     */
    @Override
    @Transactional
    public List<DoneIssuesResponseDTO> findDoneIssues(Long projectId, String status) {
        // 프로젝트 정보 조회
        Project findProject = getProjectById(projectId);

        // 해당 프로젝트에서 지정된 상태(status)인 이슈 목록
        List<DoneIssuesResponseDTO> getDoneIssue = issueRepository.getDoneIssues(findProject, status.toUpperCase());

        // 이슈에 연결된 멤버가 없는 경우, memberId를 0으로 설정
        for (DoneIssuesResponseDTO doneIssue : getDoneIssue) {
            Optional<ProjectMember> projectMember = projectMemberRepository.findById(doneIssue.getMemberId());

            if (projectMember.isEmpty()) {
                doneIssue.setMemberId(0L);
            }
        }

        return getDoneIssue;
    }

    /**
     * 7.6 릴리즈 노트별 연결된 이슈 조회
     *
     * @author chaeanna
     * @date 2023-07-08
     */
    @Override
    @Transactional
    public List<ConnectionIssuesResponseDTO> findConnectIssues(Long projectId, Long releaseId) {
        // 프로젝트 정보 조회
        Project findProject = getProjectById(projectId);

        // 릴리즈 노트 정보 조회
        ReleaseNote findReleaseNote = getReleaseNoteById(releaseId);

        // 특정 릴리즈 노트에 연결된 이슈 목록
        List<ConnectionIssuesResponseDTO> getConnectionIssues = issueRepository.getConnectionIssues(findProject, findReleaseNote);

        return getConnectionIssues;
    }

    /**
     * 7.7 이슈별 조회
     *
     * @author chaeanna
     * @date 2023-07-09
     * @param email 사용자 이메일
     */
    @Override
    @Transactional
    public IssueDetailsDTO findIssue(Long issueId, String email) {
        // 이슈 정보 조회
        Issue issue = getIssueById(issueId);

        // Token UserInfo
        User user = getUserByEmail(email);

        // 사용자가 프로젝트 멤버인지 확인, 해당하는 프로젝트 멤버 정보 가져옴.
        Long memberId = getProjectMemberByUserAndProject(user, issue.getProject()).getMemberId();
        ProjectMember member = getProjectMemberById(memberId);

        // 프로젝트 멤버가 이슈를 조회하는 경우, edit 상태 변경
        updateIssueEdit(issue, member);

        // 이슈의 의견 리스트, 해당 프로젝트 멤버의 의견은 삭제 여부를 포함
        List<OpinionInfoResponseDTO> opinionRes = getIssueOpinionsWithDeleteYN(issue, memberId);

        // 프로젝트의 모든 멤버 리스트
        List<GetMembersDataDTO> memberRes = getProjectMembers(member.getProject());

        IssueDetailsDTO getIssue = createIssueDetails(member, issue, memberRes, opinionRes);

        return getIssue;
    }

    /**
     * 7.8 이슈 상태 변경
     *
     * @author chaeanna
     * @date 2023-07-08
     * @param lifeCycle 변경할 이슈의 상태 ("NOT_STARTED", "IN_PROGRESS", "DONE" 중 하나로 대소문자 구분 없이 입력)
     */
    @Override
    @Transactional
    public String modifyIssueLifeCycle(Long issueId, String lifeCycle) {
        // 이슈 정보 조회
        Issue issue = getIssueById(issueId);

        // 연결된 이슈가 있을 경우 상태 변경이 불가능, 예외 발생
        if (issue.getRelease() != null) {
            throw new CustomException(CONNECTED_ISSUE_EXISTS);
        }

        // 이슈의 상태 변경
        String result = changeLifeCycle(issue, lifeCycle.toUpperCase());

        return result;
    }

    /**
     * 8.1 이슈 의견 추가
     *
     * @author chaeanna
     * @date 2023-07-08
     * @param email 사용자의 이메일
     */
    @Override
    @Transactional
    public List<OpinionInfoResponseDTO> registerOpinion(Long issueId, String email, RegisterOpinionRequestDTO issueOpinionReq) {
        // 이슈 정보 조회
        Issue issue = getIssueById(issueId);
        // 사용자 정보 조회
        User user = getUserByEmail(email);

        // 사용자와 이슈의 프로젝트 멤버 정보 조회
        Long memberId = getProjectMemberByUserAndProject(user, issue.getProject()).getMemberId();
        ProjectMember member = getProjectMemberById(memberId);

        // 의견 등록
        IssueOpinion issueOpinion = saveOpinion(issue, member, issueOpinionReq.getOpinion());

        // 등록된 의견 리스트 조회 (삭제 여부 포함)
        List<OpinionInfoResponseDTO> opinionRes = getIssueOpinionsWithDeleteYN(issue, memberId);

        return opinionRes;
    }

    /**
     * 8.2 이슈 의견 삭제
     *
     * @author chaeanna
     * @date 2023-07-08
     * @param email 사용자의 이메일
     * @throws CustomException 삭제 권한이 없을 경우 예외 발생
     */
    @Override
    @Transactional
    public List<OpinionInfoResponseDTO> deleteOpinion(Long opinionId, String email) {
        // 사용자 정보 조회
        User user = getUserByEmail(email);

        // 의견 정보 조회
        IssueOpinion issueOpinion = issueOpinionRepository.findById(opinionId).orElseThrow(() -> new CustomException(NOT_EXISTS_ISSUE_OPINION));

        // 접근 유저가 해당 의견 작성자인지 확인하여 삭제 권한이 있으면 삭제
        if (equalsMember(user, issueOpinion)) {
            // 의견 soft delete
            issueOpinionRepository.deleteById(opinionId);

            // 삭제 후의 이슈에 대한 모든 의견 정보 조회 (삭제 여부 포함)
            Long memberId = getProjectMemberByUserAndProject(user, issueOpinion.getIssue().getProject()).getMemberId();
            List<OpinionInfoResponseDTO> opinionRes = getIssueOpinionsWithDeleteYN(issueOpinion.getIssue(), memberId);
            return opinionRes;
        } else {
            throw new CustomException(NOT_ISSUE_COMMENTER);
        }
    }

    // =================================================================================================================

    /**
     * memberId로 프로젝트 멤버 가져오기
     *
     * @author chaeanna
     * @date 2023-07-05
     * @param memberId 조회할 프로젝트 멤버의 식별 번호
     * @return ProjectMember 프로젝트 멤버 정보
     * @throws CustomException 프로젝트 멤버가 존재하지 않을 경우 예외 발생
     */
    private ProjectMember getProjectMemberById(Long memberId) {
        return projectMemberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(BaseResponseStatus.NOT_EXISTS_PROJECT_MEMBER));
    }

    /**
     * 사용자와 프로젝트로 프로젝트 멤버 가져오기
     *
     * @author chaeanna
     * @date 2023-07-05
     * @param user 사용자 정보
     * @param project 프로젝트 정보
     * @return ProjectMember 프로젝트 멤버 정보
     */
    private ProjectMember getProjectMemberByUserAndProject(User user, Project project) {
        return projectMemberRepository.findByUserAndProject(user, project);
    }

    /**
     * projectId로 프로젝트 가져오기
     *
     * @author chaeanna
     * @date 2023-07-05
     * @param projectId 조회할 프로젝트의 식별 번호
     * @return Project 프로젝트 정보
     * @throws CustomException 프로젝트가 존재하지 않을 경우 예외 발생
     */
    private Project getProjectById(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomException(BaseResponseStatus.NOT_EXISTS_PROJECT));
    }

    /**
     * 이슈 저장
     *
     * @author chaeanna
     * @date 2023-07-05
     * @param issueInfoReq 이슈 정보를 담고 있는 요청 DTO
     * @param project 이슈가 속하는 프로젝트 정보
     * @param projectMember 이슈를 등록하는 프로젝트 멤버 정보
     * @return Issue 생성된 이슈 정보
     */
    private Issue createIssueNumAndSaveIssue(IssueInfoRequestDTO issueInfoReq, Project project, ProjectMember projectMember) {
        // 프로젝트에 저장된 마지막 이슈 번호를 조회 후 + 1
        Long number = issueRepository.getIssueNum(project) + 1;

        // 새로운 이슈 생성
        Issue issue = issueRepository.save(Issue.builder()
                .title(issueInfoReq.getTitle())
                .content(issueInfoReq.getContent())
                .tag(Tag.valueOf(issueInfoReq.getTag().toUpperCase()))
                .endDate(issueInfoReq.getEndDate())
                .project(project)
                .member(projectMember)
                .build());

        // 이슈 번호 정보 생성, 이슈 연결
        IssueNum issueNum = saveIssueNumberForIssue(project, issue, number);

        // 이슈 업데이트
        issue.updateIssueNum(issueNum);

        return issue;
    }

    /**
     * 이슈 번호 저장 및 연결
     *
     * @author chaeanna
     * @date 2023-07
     * @param project 이슈가 속하는 프로젝트 정보
     * @param newIssue 새로 생성된 이슈 정보
     * @param number 새로 생성된 이슈 번호
     * @return IssueNum 생성된 이슈 번호 정보
     */
    private IssueNum saveIssueNumberForIssue(Project project, Issue newIssue, Long number) {
        // 새로운 이슈 번호 정보 생성, 프로젝트와 이슈 연결
        return issueNumRepository.save(IssueNum.builder()
                .issue(newIssue)
                .project(project)
                .issueNum(number)
                .build());
    }

    /**
     * issueId로 issue 가져오기
     *
     * @author chaeanna
     * @date 2023-07-05
     * @param issueId 조회할 이슈의 식별 번호
     * @return Issue 조회된 이슈 정보
     * @throws CustomException 이슈가 존재하지 않을 경우 예외 발생
     */
    private Issue getIssueById(Long issueId) {
        return issueRepository.findById(issueId)
                .orElseThrow(() -> new CustomException(NOT_EXISTS_ISSUE));
    }

    /**
     * email로 user 가져오기
     *
     * @author chaeanna
     * @date 2023-07-05
     * @param email 조회할 사용자의 이메일 주소
     * @return User 조회된 사용자 정보
     * @throws CustomException 사용자가 존재하지 않을 경우 예외 발생
     */
    private User getUserByEmail(String email) {
        return userRepository.findOneByEmail(email)
                .orElseThrow(() -> new CustomException(NOT_EXISTS_USER));
    }

    /**
     * 편집 여부를 멤버의 역할에 따라 결정
     *
     * @author chaeanna
     * @date 2023-07-05
     * @param memberId 조회할 멤버의 식별 번호
     * @return 편집 가능 여부 ('Y' 또는 'N')
     * @throws CustomException 멤버가 존재하지 않을 경우 예외 발생
     */
    private char decideEditStatus(Long memberId) {
        // memberId로 해당 프로젝트 멤버 정보를 가져옵니다.
        ProjectMember projectMember = getProjectMemberById(memberId);

        // 멤버의 포지션이 'M'인 경우 'Y'(편집 가능)을 반환하고, 그 외에는 'N'(편집 불가능)을 반환합니다.
        return (projectMember.getPosition() == 'M') ? 'Y' : 'N';
    }

    /**
     * 이슈 필터링 및 배포 상태 설정
     *
     * @author chaeanna
     * @date 2023-07-08
     * @param issues 이슈 리스트
     * @param lifeCycle 필터링할 배포 상태 (NOT_STARTED, IN_PROGRESS, DONE 중 하나로 대소문자 구분 없이 입력)
     * @return IssueInfoResponseDTO 필터링된 이슈 리스트
     */
    private List<IssueInfoResponseDTO> filterAndSetDeployStatus(List<IssueInfoResponseDTO> issues, String lifeCycle) {
        return issues.stream()
                .filter(issue -> lifeCycle.equalsIgnoreCase(issue.getLifeCycle()))
                .peek(issueInfoRes -> {
                    // 이슈 정보 조회
                    Issue issue = getIssueById(issueInfoRes.getIssueId());

                    // 이슈에 연결된 멤버의 식별 번호 조회
                    Long memberId = issueInfoRes.getMemberId();

                    // 멤버 식별 번호가 null이 아니면서 프로젝트 멤버가 존재하지 않을 경우, 멤버 식별 번호를 0으로 설정
                    if (memberId != null && projectMemberRepository.findById(memberId).isEmpty()) {
                        issueInfoRes.setMemberId(0L);
                    }
                    // 연결된 릴리즈 존재, 릴리즈의 배포 상태가 "DEPLOYED"인 경우, deployYN 'Y' 설정, 그렇지 않은 경우 'N' 설정
                    if (issue.getRelease() != null && "DEPLOYED".equalsIgnoreCase(String.valueOf(issue.getRelease().getDeployStatus()))) {
                        issueInfoRes.setDeployYN('Y');
                    } else {
                        issueInfoRes.setDeployYN('N');
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * releaseId로 releaseNote 찾기
     *
     * @author chaeanna
     * @date 2023-07-08
     * @param releaseId 조회할 릴리즈 노트의 식별 번호
     * @return ReleaseNote 조회된 릴리즈 노트 정보
     * @throws CustomException 릴리즈 노트가 존재하지 않을 경우 예외 발생
     */
    private ReleaseNote getReleaseNoteById(Long releaseId) {
        return releaseRepository.findById(releaseId)
                .orElseThrow(() -> new CustomException(NOT_EXISTS_RELEASE_NOTE));
    }

    /**
     * 이슈 상세 정보 생성
     *
     * @author chaeanna
     * @date 2023-07-09
     * @param issue 이슈 정보
     * @param memberRes 멤버 리스트
     * @param opinionRes 의견 리스트
     * @return IssueDetailsDTO 생성된 이슈 상세 정보
     */
    private IssueDetailsDTO createIssueDetails(ProjectMember member, Issue issue, List<GetMembersDataDTO> memberRes, List<OpinionInfoResponseDTO> opinionRes) {
        // 이슈 상세 정보 생성
        IssueDetailsDataDTO getIssue = IssueMapper.INSTANCE.mapToGetIssue(issue, memberRes, opinionRes);

        // 이슈에 연결된 담당자의 식별 번호 조회
        Long memberId = getIssue.getManager();

        // 담당자 식별 번호가 null이 아니면서 프로젝트 멤버가 존재하지 않는 경우, 담당자 식별 번호를 0으로 설정
        if (memberId != null && projectMemberRepository.findById(memberId).isEmpty()) {
            getIssue.setManager(0L);
        }

        // 연결된 릴리즈 존재, 릴리즈의 배포 상태가 "DEPLOYED"인 경우, deployYN 'Y' 설정, 그렇지 않은 경우 'N' 설정
        ReleaseNote release = issue.getRelease();
        getIssue.setDeployYN(release != null && "DEPLOYED".equals(String.valueOf(release.getDeployStatus())) ? 'Y' : 'N');

        return IssueDetailsDTO.builder().pmCheck(member.getPosition() == 'L' ? 'Y' : 'N').issueDetails(getIssue).build();
    }

    /**
     * 이슈 편집 상태 업데이트
     *
     * @author chaeanna
     * @date 2023-07-09
     * @param issue 이슈 정보
     * @param member 프로젝트 멤버 정보
     */
    private void updateIssueEdit(Issue issue, ProjectMember member) {
        Project project = issue.getProject();

        // 멤버의 포지션 'L'인 경우 이슈의 편집 상태를 'N'(편집 불가능)로 업데이트
        boolean hasEditor = project.getMembers().stream()
                .anyMatch(m -> m.getPosition() == 'L' && m.getMemberId() == member.getMemberId());

        if (hasEditor) {
            issue.updateIssueEdit('N');
        }
    }

    /**
     * 이슈의 의견 목록 조회 및 삭제 가능 여부 설정
     *
     * @author chaeanna
     * @date 2023-07-09
     * @param issue 이슈 정보
     * @param memberId 멤버 식별 번호
     * @return OpinionInfoResponseDTO 이슈의 의견 목록
     */
    private List<OpinionInfoResponseDTO> getIssueOpinionsWithDeleteYN(Issue issue, Long memberId) {
        // 이슈의 의견 목록 조회
        List<OpinionInfoResponseDTO> issueOpinion = issueRepository.getIssueOpinion(issue);

        // 각 의견에 대해 주어진 멤버 식별 번호와 비교하여 삭제 가능 여부 설정
        for (OpinionInfoResponseDTO opinion : issueOpinion) {
            // 의견의 작성자가 일치하는 경우 deleteYN 'Y'로 설정, 그렇지 않은 경우 'N' 설정
            opinion.setDeleteYN(opinion.getMemberId().equals(memberId) ? 'Y' : 'N');
        }

        return issueOpinion;
    }

    /**
     * 프로젝트 멤버 리스트 조회
     *
     * @author chaeanna
     * @date 2023-07-09
     * @param project 프로젝트 정보
     * @return GetMembers 프로젝트의 멤버 리스트
     */
    private List<GetMembersDataDTO> getProjectMembers(Project project) {
        // 프로젝트에 속한 멤버 리스트를 조회
        List<GetMembersDataDTO> issueMember = projectRepository.getMemberList(project);

        return issueMember;
    }

    /**
     * 이슈 상태 변경
     *
     * @author chaeanna
     * @date 2023-07-08
     * @param issue 이슈 정보
     * @param lifeCycle 변경할 상태 ("NOT_STARTED", "IN_PROGRESS", "DONE" 중 하나로 대소문자 구분 없이 입력)
     * @return String "이슈 상태 변경이 완료되었습니다."
     */
    private String changeLifeCycle(Issue issue, String lifeCycle) {
        // 이슈의 상태를 주어진 상태로 변경
        issue.updateLifeCycle(lifeCycle);
        issueRepository.save(issue);

        return "이슈 상태 변경이 완료되었습니다.";
    }

    /**
     * 특정 이슈에 대한 의견을 저장하는 메서드입니다.
     *
     * @author chaeanna
     * @date 2023-07-08
     * @param issue 이슈 정보
     * @param member 프로젝트 멤버 정보
     * @param opinion 의견 내용
     * @return IssueOpinion 저장된 의견 정보
     */
    private IssueOpinion saveOpinion(Issue issue, ProjectMember member, String opinion) {
        // 의견 저장
        return issueOpinionRepository.save(IssueOpinion.builder()
                .opinion(opinion)
                .issue(issue)
                .member(member)
                .build());
    }

    /**
     * 특정 사용자가 해당 이슈 의견 작성자인지 확인하는 메서드입니다.
     *
     * @author chaeanna
     * @date 2023-07-08
     * @param user 사용자 정보
     * @param opinion 이슈 의견 정보
     * @return boolean 해당 사용자가 이슈 의견 작성자인지 여부
     */
    private boolean equalsMember(User user, IssueOpinion opinion) {
        // 이슈가 속한 프로젝트 정보 조회
        Project project = opinion.getIssue().getProject();

        // 사용자의 프로젝트 멤버 정보 조회
        Long accessMember = getProjectMemberByUserAndProject(user, project).getMemberId();

        // memberid와 이슈 의견 작성자의 memberId 비교하여 일치 여부 반환
        return Objects.equals(accessMember, opinion.getMember().getMemberId());
    }

}
