package com.momentum.releaser.domain.project.application;

import com.momentum.releaser.domain.project.dao.ProjectMemberRepository;
import com.momentum.releaser.domain.project.dao.ProjectRepository;
import com.momentum.releaser.domain.project.domain.Project;
import com.momentum.releaser.domain.project.domain.ProjectMember;
import com.momentum.releaser.domain.project.dto.ProjectResDto.GetMembersRes;
import com.momentum.releaser.domain.project.mapper.ProjectMemberMapper;
import com.momentum.releaser.domain.release.dao.approval.ReleaseApprovalRepository;
import com.momentum.releaser.domain.release.dao.release.ReleaseRepository;
import com.momentum.releaser.domain.release.domain.ReleaseApproval;
import com.momentum.releaser.domain.release.domain.ReleaseNote;
import com.momentum.releaser.domain.user.dao.UserRepository;
import com.momentum.releaser.domain.user.domain.User;
import com.momentum.releaser.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.momentum.releaser.global.config.BaseResponseStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectMemberServiceImpl implements ProjectMemberService {

    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ReleaseApprovalRepository releaseApprovalRepository;
    private final ReleaseRepository releaseRepository;

    /**
     * 4.1 프로젝트 멤버 조회
     */
    @Override
    @Transactional
    public List<GetMembersRes> getMembers(Long projectId, String email) {
        //Token UserInfo
        User user = findUserByEmail(email);
        Project project = findProject(projectId);

        ProjectMember accessMember = findProjectMember(user, project);

        List<GetMembersRes> getMembersRes = projectMemberRepository.findByProject(project)
                .stream()
                .map(member -> createGetMembersRes(member, accessMember))
                .collect(Collectors.toList());

        return getMembersRes;
    }

    //email로 User 조회
    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new CustomException(NOT_EXISTS_USER));
    }

    //projectId로 project 조회
    private Project findProject(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomException(NOT_EXISTS_PROJECT));
    }

    //GetMembersRes 객체를 생성
    private GetMembersRes createGetMembersRes(ProjectMember projectMember, ProjectMember accessMember) {
        char position = accessMember.getPosition();
        char deleteYN = (position == 'L') ? 'Y' : 'N';

        GetMembersRes getMembersRes = ProjectMemberMapper.INSTANCE.toGetMembersRes(projectMember);
        getMembersRes.setDeleteYN(deleteYN);

        return getMembersRes;
    }



    /**
     * 4.2 프로젝트 멤버 추가
     */
    @Override
    @Transactional
    public String addMember(String link, String email) {
        //Token UserInfo
        User user = findUserByEmail(email);

        //link check
        Project project = projectRepository.findByLink(link).orElseThrow(() -> new CustomException(NOT_EXISTS_LINK));

        //projectMember 존재여부 확인
        if (isProjectMember(user, project)) {
            throw new CustomException(ALREADY_EXISTS_PROJECT_MEMBER);
        }
        // member 추가
        ProjectMember member = addProjectMember(project, user);
        // approval 추가
        addReleaseApprovalsForProjectMember(member, project);
        return "프로젝트 참여가 완료되었습니다.";
    }

    private boolean isProjectMember(User user, Project project) {
        return findProjectMember(user, project) != null;
    }

    //프로젝트 멤버 추가
    private ProjectMember addProjectMember(Project project, User user) {
        ProjectMember projectMember = ProjectMember.builder()
                .position('M')
                .user(user)
                .project(project)
                .build();

        return projectMemberRepository.save(projectMember);
    }

    private void addReleaseApprovalsForProjectMember(ProjectMember member, Project project) {
        List<ReleaseNote> releaseNotes = releaseRepository.findAllByProject(project);
        if (releaseNotes != null) {
            for (ReleaseNote releaseNote : releaseNotes) {
                ReleaseApproval releaseApproval = ReleaseApproval.builder()
                        .member(member)
                        .release(releaseNote)
                        .build();

                releaseApprovalRepository.save(releaseApproval);
            }
        }
    }

    /**
     * 4.3 프로젝트 멤버 제거
     */
    @Override
    @Transactional
    public String deleteMember(Long memberId, String email) {
        User user = findUserByEmail(email);
        ProjectMember projectMember = findProjectMemberById(memberId);

        if (!isProjectLeader(user, projectMember.getProject())) {
            throw new CustomException(NOT_PROJECT_PM);
        }

        projectMemberRepository.deleteById(projectMember.getMemberId());
        releaseApprovalRepository.deleteByReleaseApproval();
        return "프로젝트 멤버가 제거되었습니다.";
    }

    private ProjectMember findProjectMemberById(Long memberId) {
        return projectMemberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(NOT_EXISTS_PROJECT_MEMBER));
    }

    private boolean isProjectLeader(User user, Project project) {
        ProjectMember accessMember = findProjectMember(user, project);
        return accessMember != null && accessMember.getPosition() == 'L';
    }


    /**
     * 4.4 프로젝트 멤버 탈퇴
     */
    @Override
    @Transactional
    public String withdrawMember(Long projectId, String email) {
        User user = findUserByEmail(email);

        Project project = findProject(projectId);

        //project member 찾기
        ProjectMember member = findProjectMember(user, project);
        //project member status = 'N'
        projectMemberRepository.deleteById(member.getMemberId());
        //approval delete
        releaseApprovalRepository.deleteByReleaseApproval();

        return "프로젝트 탈퇴가 완료되었습니다.";
    }

    //project member 찾기
    private ProjectMember findProjectMember(User user, Project project) {
        return projectMemberRepository.findByUserAndProject(user, project);
    }

}
