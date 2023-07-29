package com.momentum.releaser.domain.issue.application;

import com.momentum.releaser.domain.issue.dao.IssueNumRepository;
import com.momentum.releaser.domain.issue.dao.IssueOpinionRepository;
import com.momentum.releaser.domain.issue.dao.IssueRepository;
import com.momentum.releaser.domain.issue.domain.*;
import com.momentum.releaser.domain.issue.dto.IssueRequestDto.IssueInfoRequestDTO;
import com.momentum.releaser.domain.issue.dto.IssueRequestDto.RegisterOpinionReq;
import com.momentum.releaser.domain.issue.mapper.IssueMapper;
import com.momentum.releaser.domain.project.dao.ProjectMemberRepository;
import com.momentum.releaser.domain.project.dao.ProjectRepository;
import com.momentum.releaser.domain.project.domain.Project;
import com.momentum.releaser.domain.project.domain.ProjectMember;
import com.momentum.releaser.domain.project.dto.ProjectDataDto.GetMembers;
import com.momentum.releaser.domain.release.dao.release.ReleaseRepository;
import com.momentum.releaser.domain.release.domain.ReleaseNote;
import com.momentum.releaser.domain.user.dao.UserRepository;
import com.momentum.releaser.domain.user.domain.User;
import com.momentum.releaser.global.config.BaseResponseStatus;
import com.momentum.releaser.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.momentum.releaser.domain.issue.dto.IssueResponseDto.*;
import static com.momentum.releaser.global.config.BaseResponseStatus.*;

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
     */
    @Override
    @Transactional
    public IssueIdResponseDTO addIssue(Long projectId, IssueInfoRequestDTO createReq) {
        ProjectMember projectMember = null;
        // memberId not null
        if (createReq.getMemberId() != null) {
            projectMember = getProjectMemberById(createReq.getMemberId());
        }

        Project project = getProjectById(projectId);
        Issue newIssue = createIssueNumAndSaveIssue(createReq, project, projectMember);

        return IssueIdResponseDTO.builder()
                .issueId(newIssue.getIssueId())
                .build();
    }

    /**
     * 7.2 이슈 수정
     */
    @Override
    @Transactional
    public String modifyIssue(Long issueId, String email, IssueInfoRequestDTO updateReq) {
        // 이슈 정보 조회
        Issue issue = getIssueById(issueId);

        //Token UserInfo
        User user = getUserByEmail(email);
        ProjectMember projectMember = getProjectMemberByUserAndProject(user, issue.getProject());

        //edit check
        //접근한 유저가 멤버일 경우 edit 상태 변경
        char edit = decideEditStatus(projectMember.getMemberId());

        ProjectMember manager = null;
        // 담당자 memberId가 null이 아닌 경우 프로젝트 멤버 조회
        if (updateReq.getMemberId() != null) {
            manager = getProjectMemberById(updateReq.getMemberId());
        }

        // 이슈 업데이트
        issue.updateIssue(updateReq, edit, manager);
        issueRepository.save(issue);

        return "이슈 수정이 완료되었습니다.";
    }

    /**
     * 7.3 이슈 제거
     */
    @Override
    @Transactional
    public String removeIssue(Long issueId) {
        //issue
        Issue issue = getIssueById(issueId);

        //issue와 연결된 릴리즈가 있으면 삭제 안됨
        if (issue.getRelease() != null) {
            Long releaseId = issue.getRelease().getReleaseId();
            throw new CustomException(CONNECTED_RELEASE_EXISTS, releaseId);
        }

        issueNumRepository.deleteById(issue.getIssueNum().getIssueNumId());
        issueRepository.deleteById(issue.getIssueId());

        return "이슈가 삭제되었습니다.";
    }

    /**
     * 7.4 프로젝트별 모든 이슈 조회
     */
    @Override
    @Transactional
    public AllIssueListResponseDTO findAllIssues(Long projectId) {
        Project findProject = getProjectById(projectId);
        List<IssueInfoResponseDTO> getAllIssue = issueRepository.getIssues(findProject);

        List<IssueInfoResponseDTO> notStartedList = filterAndSetDeployStatus(getAllIssue, "NOT_STARTED");
        List<IssueInfoResponseDTO> inProgressList = filterAndSetDeployStatus(getAllIssue, "IN_PROGRESS");
        List<IssueInfoResponseDTO> doneList = filterAndSetDeployStatus(getAllIssue, "DONE");

        return AllIssueListResponseDTO.builder()
                .getNotStartedList(notStartedList)
                .getInProgressList(inProgressList)
                .getDoneList(doneList)
                .build();
    }

    /**
     * 7.5 프로젝트별 해결 & 미연결 이슈 조회
     */
    @Override
    @Transactional
    public List<DoneIssuesResponseDTO> findDoneIssues(Long projectId, String status) {
        Project findProject = getProjectById(projectId);
        List<DoneIssuesResponseDTO> getDoneIssue = issueRepository.getDoneIssues(findProject, status.toUpperCase());

        for (DoneIssuesResponseDTO getDoneIssues : getDoneIssue) {
            Optional<ProjectMember> projectMember = projectMemberRepository.findById(getDoneIssues.getMemberId());

            if (projectMember.isEmpty()) {
                getDoneIssues.setMemberId(0L);
            }

        }

        return getDoneIssue;
    }
    /**
     * 7.6 릴리즈 노트별 연결된 이슈 조회
     */
    @Override
    @Transactional
    public List<ConnectionIssuesResponseDTO> findConnectIssues(Long projectId, Long releaseId) {
        Project findProject = getProjectById(projectId);
        ReleaseNote findReleaseNote = getReleaseNoteById(releaseId);

        List<ConnectionIssuesResponseDTO> getConnectionIssues = issueRepository.getConnectionIssues(findProject, findReleaseNote);

        return getConnectionIssues;
    }

    /**
     * 7.7 이슈별 조회
     */
    @Override
    @Transactional
    public IssueDetailsDTO findIssue(Long issueId, String email) {
        Issue issue = getIssueById(issueId);
        //Token UserInfo
        User user = getUserByEmail(email);

        Long memberId = getProjectMemberByUserAndProject(user, issue.getProject()).getMemberId();
        ProjectMember member = getProjectMemberById(memberId);

        //pm이 조회할 경우 edit 상태 변경
        updateIssueEdit(issue, member);

        //의견 리스트
        List<OpinionInfoResponseDTO> opinionRes = getIssueOpinionsWithDeleteYN(issue, memberId);

        //프로젝트 멤버 리스트
        List<GetMembers> memberRes = getProjectMembers(member.getProject());
        IssueDetailsDTO getIssue = createIssueDetails(issue, memberRes, opinionRes);

        return getIssue;
    }

    /**
     * 7.8 이슈 상태 변경
     */
    @Override
    @Transactional
    public String modifyIssueLifeCycle(Long issueId, String lifeCycle) {
        //issue 정보
        Issue issue = getIssueById(issueId);

        //연결된 이슈가 있을 경우 validation
        if (issue.getRelease() != null) {
            throw new CustomException(CONNECTED_ISSUE_EXISTS);
        }

        //이슈 상태 변경
        String result = changeLifeCycle(issue, lifeCycle.toUpperCase());
        return result;
    }

    /**
     * 8.1 이슈 의견 추가
     */
    @Override
    @Transactional
    public List<OpinionInfoResponseDTO> registerOpinion(Long issueId, String email, RegisterOpinionReq issueOpinionReq) {
        //issue
        Issue issue = getIssueById(issueId);
        //Token UserInfo
        User user = getUserByEmail(email);

        Long memberId = getProjectMemberByUserAndProject(user, issue.getProject()).getMemberId();
        //project member
        ProjectMember member = getProjectMemberById(memberId);

        //save opinion
        IssueOpinion issueOpinion = saveOpinion(issue, member, issueOpinionReq.getOpinion());

        List<OpinionInfoResponseDTO> opinionRes = getIssueOpinionsWithDeleteYN(issue, memberId);

        return opinionRes;
    }

    private IssueOpinion saveOpinion(Issue issue, ProjectMember member, String opinion) {
        //Add issue
        return issueOpinionRepository.save(IssueOpinion.builder()
                .opinion(opinion)
                .issue(issue)
                .member(member)
                .build());
    }

    /**
     * 8.2 이슈 의견 삭제
     */
    @Override
    @Transactional
    public List<OpinionInfoResponseDTO> deleteOpinion(Long opinionId, String email) {
        //Token UserInfo
        User user = getUserByEmail(email);
        //opinion
        IssueOpinion issueOpinion = issueOpinionRepository.findById(opinionId).orElseThrow(() -> new CustomException(NOT_EXISTS_ISSUE_OPINION));
        //접근 유저가 해당 의견 작성자면 삭제
        if (equalsMember(user, issueOpinion)) {
            //opinion soft delete
            issueOpinionRepository.deleteById(opinionId);
            Long memberId = getProjectMemberByUserAndProject(user, issueOpinion.getIssue().getProject()).getMemberId();
            List<OpinionInfoResponseDTO> opinionRes = getIssueOpinionsWithDeleteYN(issueOpinion.getIssue(), memberId);
            return opinionRes;
        }
        else {
            throw new CustomException(NOT_ISSUE_COMMENTER);
        }

    }

    private boolean equalsMember(User user, IssueOpinion opinion) {
        Project project = opinion.getIssue().getProject();
        Long accessMember = getProjectMemberByUserAndProject(user, project).getMemberId();
        return Objects.equals(accessMember, opinion.getMember().getMemberId());
    }


    // =================================================================================================================

    // memberId로 프로젝트 멤버 찾기
    private ProjectMember getProjectMemberById(Long memberId) {
        return projectMemberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(BaseResponseStatus.NOT_EXISTS_PROJECT_MEMBER));
    }

    private ProjectMember getProjectMemberByUserAndProject (User user, Project project) {
        return projectMemberRepository.findByUserAndProject(user, project);
    }

    // projectId로 프로젝트 찾기
    private Project getProjectById(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomException(BaseResponseStatus.NOT_EXISTS_PROJECT));
    }

    // 이슈 저장
    private Issue createIssueNumAndSaveIssue(IssueInfoRequestDTO issueInfoReq, Project project, ProjectMember projectMember) {
        Long number = issueRepository.getIssueNum(project) + 1;

        Issue issue = issueRepository.save(Issue.builder()
                .title(issueInfoReq.getTitle())
                .content(issueInfoReq.getContent())
                .tag(Tag.valueOf(issueInfoReq.getTag().toUpperCase()))
                .endDate(issueInfoReq.getEndDate())
                .project(project)
                .member(projectMember)
                .build());

        IssueNum issueNum = saveIssueNumberForIssue(project, issue, number);

        issue.updateIssueNum(issueNum);
        return issue;
    }

    private IssueNum saveIssueNumberForIssue(Project project, Issue newIssue, Long number) {
        return issueNumRepository.save(IssueNum.builder()
                .issue(newIssue)
                .project(project)
                .issueNum(number)
                .build());
    }

    // issueId로 issue 조회
    private Issue getIssueById(Long issueId) {
        return issueRepository.findById(issueId)
                .orElseThrow(() -> new CustomException(NOT_EXISTS_ISSUE));
    }

    //email로 user 조회
    private User getUserByEmail(String email) {
        return userRepository.findOneByEmail(email)
                .orElseThrow(() -> new CustomException(NOT_EXISTS_USER));

    }

    // 편집 여부를 멤버의 역할에 따라 결정하는 메서드
    private char decideEditStatus(Long memberId) {
        // memberId로 해당 프로젝트 멤버 정보 가져옴
        ProjectMember projectMember = getProjectMemberById(memberId);

        // 멤버의 포지션이 'M'인 경우 'Y'(편집 가능) 반환, 그 외에는 'N'(편집 불가능) 반환
        return (projectMember.getPosition() == 'M') ? 'Y' : 'N';
    }

    // 이슈 필터링 및 배포 상태 설정
    private List<IssueInfoResponseDTO> filterAndSetDeployStatus(List<IssueInfoResponseDTO> issues, String lifeCycle) {
        return issues.stream()
                .filter(issue -> lifeCycle.equals(issue.getLifeCycle()))
                .peek(issueInfoRes -> {
                    Issue issue = getIssueById(issueInfoRes.getIssueId());
                    Long memberId = issueInfoRes.getMemberId();
                    if (memberId != null && projectMemberRepository.findById(memberId).isEmpty()) {
                        issueInfoRes.setMemberId(0L);
                    }
                    if (issue.getRelease() != null && "DEPLOYED".equals(String.valueOf(issue.getRelease().getDeployStatus()))) {
                        issueInfoRes.setDeployYN('Y');
                    } else {
                        issueInfoRes.setDeployYN('N');
                    }
                })
                .collect(Collectors.toList());
    }

    //releaseId로 releaseNote 찾기
    private ReleaseNote getReleaseNoteById(Long releaseId) {
        return releaseRepository.findById(releaseId)
                .orElseThrow(() -> new CustomException(NOT_EXISTS_RELEASE_NOTE));
    }

    private IssueDetailsDTO createIssueDetails(Issue issue, List<GetMembers> memberRes, List<OpinionInfoResponseDTO> opinionRes) {
        IssueDetailsDTO getIssue = IssueMapper.INSTANCE.mapToGetIssue(issue, memberRes, opinionRes);
        Long memberId = getIssue.getManager();

        if (memberId != null && projectMemberRepository.findById(memberId).isEmpty()) {
            getIssue.setManager(0L);
        }

        ReleaseNote release = issue.getRelease();
        getIssue.setDeployYN(release != null && "DEPLOYED".equals(String.valueOf(release.getDeployStatus())) ? 'Y' : 'N');

        return getIssue;
    }

    private void updateIssueEdit(Issue issue, ProjectMember member) {
        Project project = issue.getProject();

        boolean hasEditor = project.getMembers().stream()
                .anyMatch(m -> m.getPosition() == 'L' && m.getMemberId() == member.getMemberId());

        if (hasEditor) {
            issue.updateIssueEdit('N');
        }
    }

    private List<OpinionInfoResponseDTO> getIssueOpinionsWithDeleteYN(Issue issue, Long memberId) {
        List<OpinionInfoResponseDTO> issueOpinion = issueRepository.getIssueOpinion(issue);

        for (OpinionInfoResponseDTO opinion : issueOpinion) {
            opinion.setDeleteYN(opinion.getMemberId() == memberId ? 'Y' : 'N');
        }

        return issueOpinion;
    }

    private List<GetMembers> getProjectMembers(Project project) {
        List<GetMembers> issueMember = projectRepository.getMemberList(project);

        return issueMember;
    }

    private String changeLifeCycle(Issue issue, String lifeCycle) {
        issue.updateLifeCycle(lifeCycle);
        issueRepository.save(issue);

        return "이슈 상태 변경이 완료되었습니다.";
    }

}
