package com.momentum.releaser.domain.issue.application;

import com.momentum.releaser.domain.issue.dao.IssueNumRepository;
import com.momentum.releaser.domain.issue.dao.IssueOpinionRepository;
import com.momentum.releaser.domain.issue.dao.IssueRepository;
import com.momentum.releaser.domain.issue.domain.*;
import com.momentum.releaser.domain.issue.dto.IssueReqDto.IssueInfoReq;
import com.momentum.releaser.domain.issue.dto.IssueReqDto.RegisterOpinionReq;
import com.momentum.releaser.domain.issue.dto.IssueReqDto.UpdateLifeCycleReq;
import com.momentum.releaser.domain.project.dao.ProjectMemberRepository;
import com.momentum.releaser.domain.project.dao.ProjectRepository;
import com.momentum.releaser.domain.project.domain.Project;
import com.momentum.releaser.domain.project.domain.ProjectMember;
import com.momentum.releaser.domain.project.dto.ProjectResDto;
import com.momentum.releaser.domain.project.dto.ProjectResDto.GetMembersRes;
import com.momentum.releaser.domain.release.dao.release.ReleaseRepository;
import com.momentum.releaser.domain.release.domain.ReleaseEnum;
import com.momentum.releaser.domain.release.domain.ReleaseEnum.ReleaseDeployStatus;
import com.momentum.releaser.domain.release.domain.ReleaseNote;
import com.momentum.releaser.domain.user.dao.UserRepository;
import com.momentum.releaser.domain.user.domain.User;
import com.momentum.releaser.domain.user.dto.UserResponseDto;
import com.momentum.releaser.global.config.BaseException;
import com.momentum.releaser.global.config.BaseResponse;
import com.momentum.releaser.global.config.BaseResponseStatus;
import com.momentum.releaser.global.error.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.momentum.releaser.domain.issue.dto.IssueResDto.*;
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
    public String registerIssue(Long projectId, IssueInfoReq createReq) {
        ProjectMember projectMember = null;
        // memberId not null
        if (createReq.getMemberId() != null) {
            projectMember = findProjectMember(createReq.getMemberId());
        }
        Project project = findProject(projectId);
        Issue newIssue = saveIssue(createReq, project, projectMember);
        String result = "이슈 생성이 완료되었습니다.";
        return result;
    }

    private IssueNum saveIssueNum(Project project, Issue newIssue, Long number) {

        return issueNumRepository.save(IssueNum.builder()
                        .issue(newIssue)
                        .project(project)
                        .issueNum(number)
                .build());
    }

    // memberId로 프로젝트 멤버 찾기
    private ProjectMember findProjectMember(Long memberId) {
        return projectMemberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(BaseResponseStatus.NOT_EXISTS_PROJECT_MEMBER));
    }

    private ProjectMember findProjectMemberByUserAndProject (User user, Project project) {
        return projectMemberRepository.findByUserAndProject(user, project);
    }

    // projectId로 프로젝트 찾기
    private Project findProject(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomException(BaseResponseStatus.NOT_EXISTS_PROJECT));
    }

    // 이슈 저장
    private Issue saveIssue(IssueInfoReq issueInfoReq, Project project, ProjectMember projectMember) {
        Long number = issueRepository.getIssueNum(project) + 1;
        Issue issue = issueRepository.save(Issue.builder()
                .title(issueInfoReq.getTitle())
                .content(issueInfoReq.getContent())
                .tag(Tag.valueOf(issueInfoReq.getTag().toUpperCase()))
                .endDate(issueInfoReq.getEndDate())
                .project(project)
                .member(projectMember)
                .build());
        IssueNum issueNum = saveIssueNum(project, issue, number);
        issue.updateIssueNum(issueNum);
        return issue;
    }



    /**
     * 7.2 이슈 수정
     */
    @Override
    @Transactional
    public String updateIssue(Long issueId, String email, IssueInfoReq updateReq) {
        // 이슈 정보 조회
        Issue issue = findIssue(issueId);

        //Token UserInfo
        User user = findUserByEmail(email);
        ProjectMember projectMember = findProjectMemberByUserAndProject(user, issue.getProject());

        //edit check
        //접근한 유저가 멤버일 경우 edit 상태 변경
        char edit = editCheck(projectMember.getMemberId());

        ProjectMember manager = null;
        // 담당자 memberId가 null이 아닌 경우 프로젝트 멤버 조회
        if (updateReq.getMemberId() != null) {
            manager = findProjectMember(updateReq.getMemberId());
        }


        // 이슈 업데이트
        issue.updateIssue(updateReq, edit, manager);
        issueRepository.save(issue);

        String result = "이슈 수정이 완료되었습니다.";
        return result;
    }

    // issueId로 issue 조회
    private Issue findIssue(Long issueId) {
        return issueRepository.findById(issueId)
                .orElseThrow(() -> new CustomException(NOT_EXISTS_ISSUE));
    }

    //email로 user 조회
    private User findUserByEmail(String email) {
        return userRepository.findOneByEmail(email).orElseThrow(() -> new CustomException(NOT_EXISTS_USER));

    }

    // 멤버의 역할에 따라 edit 상태를 결정
    private char editCheck(Long memberId) {
        ProjectMember projectMember = findProjectMember(memberId);
        return (projectMember.getPosition() == 'M') ? 'Y' : 'N';
    }


    /**
     * 7.3 이슈 제거
     */
    @Override
    @Transactional
    public String deleteIssue(Long issueId) {
        //issue
        Issue issue = findIssue(issueId);


        Long releaseId = issue.getRelease().getReleaseId();
        //issue와 연결된 릴리즈가 있으면 삭제 안됨
        if (issue.getRelease() != null) {
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
    public GetIssuesList getIssues(Long projectId) {
        Project findProject = findProject(projectId);
        List<IssueInfoRes> getAllIssue = issueRepository.getIssues(findProject);

        List<IssueInfoRes> notStartedList = filterAndSetDeployStatus(getAllIssue, "NOT_STARTED");
        List<IssueInfoRes> inProgressList = filterAndSetDeployStatus(getAllIssue, "IN_PROGRESS");
        List<IssueInfoRes> doneList = filterAndSetDeployStatus(getAllIssue, "DONE");

        return GetIssuesList.builder()
                .getNotStartedList(notStartedList)
                .getInProgressList(inProgressList)
                .getDoneList(doneList)
                .build();
    }


    private List<IssueInfoRes> filterAndSetDeployStatus(List<IssueInfoRes> issues, String lifeCycle) {
        return issues.stream()
                .filter(issue -> lifeCycle.equals(issue.getLifeCycle()))
                .peek(issueInfoRes -> {
                    Issue issue = findIssue(issueInfoRes.getIssueId());
                    if (issue.getRelease() != null) {
                        String deployStatus = String.valueOf(issue.getRelease().getDeployStatus());
                        //배포 여부
                        issueInfoRes.setDeployYN(deployStatus.equals("DEPLOYED") ? 'Y' : 'N');
                    } else {
                        issueInfoRes.setDeployYN('N');
                    }

                })
                .map(issue -> modelMapper.map(issue, IssueInfoRes.class))
                .collect(Collectors.toList());
    }


    /**
     * 7.5 프로젝트별 해결 & 미연결 이슈 조회
     */
    @Override
    @Transactional
    public List<GetDoneIssues> getDoneIssues(Long projectId) {
        Project findProject = findProject(projectId);
        List<GetDoneIssues> getDoneIssue = issueRepository.getDoneIssues(findProject);

        return getDoneIssue;
    }
    /**
     * 7.6 릴리즈 노트별 연결된 이슈 조회
     */
    @Override
    @Transactional
    public List<GetConnectionIssues> getConnectRelease(Long projectId, Long releaseId) {
        Project findProject = findProject(projectId);
        ReleaseNote findReleaseNote = findReleaseNote(releaseId);

        List<GetConnectionIssues> getConnectionIssues = issueRepository.getConnectionIssues(findProject, findReleaseNote);

        return getConnectionIssues;
    }

    //releaseId로 releaseNote 찾기
    private ReleaseNote findReleaseNote(Long releaseId) {
        return releaseRepository.findById(releaseId)
                .orElseThrow(() -> new CustomException(NOT_EXISTS_RELEASE_NOTE));
    }

    /**
     * 7.7 이슈별 조회
     */
    @Override
    @Transactional
    public GetIssue getIssue(Long issueId, String email) {
        Issue issue = findIssue(issueId);
        //Token UserInfo
        User user = findUserByEmail(email);

        Long memberId = findProjectMemberByUserAndProject(user, issue.getProject()).getMemberId();
        ProjectMember member = findProjectMember(memberId);
        //pm이 조회할 경우 edit 상태 변경
        updateIssueEdit(issue, member);

        //의견 리스트
        List<OpinionInfoRes> opinionRes = getIssueOpinion(issue, memberId);

        //프로젝트 멤버 리스트
        List<GetMembersRes> memberRes = getMemberList(member.getProject());

        GetIssue getIssue = createGetIssue(issue, memberRes, opinionRes);

        return getIssue;
    }

    private GetIssue createGetIssue(Issue issue, List<GetMembersRes> memberRes, List<OpinionInfoRes> opinionRes) {

        GetIssue getIssue = mapIssueToGetIssue(issue);
        getIssue.setIssueNum(issue.getIssueNum().getIssueNum());
        getIssue.setManager(issue.getMember().getMemberId());

        if (issue.getRelease() != null) {
            String deployStatus = String.valueOf(issue.getRelease().getDeployStatus());
            getIssue.setDeployYN(deployStatus.equals("DEPLOYED") ? 'Y' : 'N');
        } else {
            getIssue.setDeployYN('N');
        }

        getIssue.setMemberList(memberRes);
        getIssue.setOpinionList(opinionRes);
        return getIssue;
    }

    private void updateIssueEdit(Issue issue, ProjectMember member) {
        Project project = issue.getProject();
        boolean found = project.getMembers().stream()
                .anyMatch(m -> m.getPosition() == 'L' && m.getMemberId() == member.getMemberId());
        if (found) {
            issue.updateIssueEdit('N');
        }
    }

    private GetIssue mapIssueToGetIssue(Issue issue) {
        return modelMapper.map(issue, GetIssue.class);
    }

    private List<OpinionInfoRes> getIssueOpinion(Issue issue, Long memberId) {
        List<OpinionInfoRes> issueOpinion = issueRepository.getIssueOpinion(issue);
        for (OpinionInfoRes opinion : issueOpinion) {
            opinion.setDeleteYN(opinion.getMemberId() == memberId ? 'Y' : 'N');
        }
        return issueOpinion;
    }

    private List<GetMembersRes> getMemberList(Project project) {
        List<GetMembersRes> issueMember = projectRepository.getMemberList(project);
        for (GetMembersRes member : issueMember) {
            member.setDeleteYN('N');
        }
        return issueMember;
    }




    /**
     * 7.8 이슈 상태 변경
     */
    @Override
    @Transactional
    public String updateLifeCycle(Long issueId, String lifeCycle) {
        //issue 정보
        Issue issue = findIssue(issueId);

        //연결된 이슈가 있을 경우 validation
        if (issue.getRelease() != null) {
            throw new CustomException(CONNECTED_ISSUE_EXISTS);
        }

        //이슈 상태 변경
        String result = changeLifeCycle(issue, lifeCycle.toUpperCase());
        return result;
    }

    private String changeLifeCycle(Issue issue, String lifeCycle) {

        issue.updateLifeCycle(lifeCycle);
        issueRepository.save(issue);
        return "이슈 상태 변경이 완료되었습니다.";

    }


    /**
     * 8.1 이슈 의견 추가
     */
    @Override
    @Transactional
    public List<OpinionInfoRes> registerOpinion(Long issueId, String email, RegisterOpinionReq issueOpinionReq) {
        //issue
        Issue issue = findIssue(issueId);
        //Token UserInfo
        User user = findUserByEmail(email);

        Long memberId = findProjectMemberByUserAndProject(user, issue.getProject()).getMemberId();
        //project member
        ProjectMember member = findProjectMember(memberId);

        //save opinion
        IssueOpinion issueOpinion = saveOpinion(issue, member, issueOpinionReq.getOpinion());

        List<OpinionInfoRes> opinionRes = getIssueOpinion(issue, memberId);

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
    public String deleteOpinion(Long opinionId, String email) {
        //Token UserInfo
        User user = findUserByEmail(email);
        //opinion
        IssueOpinion issueOpinion = issueOpinionRepository.findById(opinionId).orElseThrow(() -> new CustomException(NOT_EXISTS_ISSUE_OPINION));
        //접근 유저가 해당 의견 작성자면 삭제
        if (equalsMember(user, issueOpinion)) {
            //opinion soft delete
            issueOpinionRepository.deleteById(opinionId);
            return "해당 이슈 의견이 삭제되었습니다.";
        }
        else {
            throw new CustomException(NOT_ISSUE_COMMENTER);
        }

    }

    private boolean equalsMember(User user, IssueOpinion opinion) {
        Project project = opinion.getIssue().getProject();
        Long accessMember = findProjectMemberByUserAndProject(user, project).getMemberId();
        return Objects.equals(accessMember, opinion.getMember().getMemberId());
    }
}
