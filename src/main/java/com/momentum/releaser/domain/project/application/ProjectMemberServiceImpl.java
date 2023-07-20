package com.momentum.releaser.domain.project.application;

import com.momentum.releaser.domain.project.dao.ProjectMemberRepository;
import com.momentum.releaser.domain.project.dao.ProjectRepository;
import com.momentum.releaser.domain.project.domain.Project;
import com.momentum.releaser.domain.project.domain.ProjectMember;
import com.momentum.releaser.domain.project.dto.ProjectResDto.GetMembersRes;
import com.momentum.releaser.domain.project.mapper.ProjectMemberMapper;
import com.momentum.releaser.domain.release.dao.approval.ReleaseApprovalRepository;
import com.momentum.releaser.domain.user.dao.UserRepository;
import com.momentum.releaser.domain.user.domain.User;
import com.momentum.releaser.global.error.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
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
    private final ModelMapper modelMapper;

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

        //member check
        ProjectMember projectMember = projectMemberRepository.findByUserAndProject(user, project);


        return null;
    }

    /**
     * 4.3 프로젝트 멤버 제거
     */
    @Override
    @Transactional
    public String deleteMember(Long memberId, String email) {
        //Token UserInfo
        User user = findUserByEmail(email);

        //project member 정보
        ProjectMember projectMember = projectMemberRepository.findById(memberId).orElseThrow(() -> new CustomException(NOT_EXISTS_PROJECT_MEMBER));
        ProjectMember accessMember = findProjectMember(user, projectMember.getProject());

        //해당 프로젝트의 관리자만 멤버 제거 가능
        if (accessMember.getPosition() == 'L') {
            projectMemberRepository.deleteById(projectMember.getMemberId());
            releaseApprovalRepository.deleteByReleaseApproval();
            String result = "프로젝트 멤버가 제거되었습니다.";
            return result;
        } else {
            throw new CustomException(NOT_PROJECT_PM);
        }

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

    //project member status = 'N'
    private void deactivateProjectMember(ProjectMember member) {
        member.statusToInactive();
    }

}
