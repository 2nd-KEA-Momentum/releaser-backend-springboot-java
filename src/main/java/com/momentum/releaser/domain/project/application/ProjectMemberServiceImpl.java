package com.momentum.releaser.domain.project.application;

import com.momentum.releaser.domain.project.dao.ProjectMemberRepository;
import com.momentum.releaser.domain.project.dao.ProjectRepository;
import com.momentum.releaser.domain.project.domain.Project;
import com.momentum.releaser.domain.project.domain.ProjectMember;
import com.momentum.releaser.domain.project.dto.ProjectResDto;
import com.momentum.releaser.domain.project.dto.ProjectResDto.GetMembersRes;
import com.momentum.releaser.domain.user.dao.UserRepository;
import com.momentum.releaser.domain.user.domain.User;
import com.momentum.releaser.global.config.BaseResponseStatus;
import com.momentum.releaser.global.error.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
    private final ModelMapper modelMapper;

    /**
     * 4.1 프로젝트 멤버 조회
     */
    @Override
    @Transactional
    public List<GetMembersRes> getMembers(Long memberId, Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomException(NOT_EXISTS_PROJECT));


        List<GetMembersRes> getMembersRes = projectMemberRepository.findByProject(project)
                .stream()
                .map(member -> createGetMembersRes(member, memberId))
                .collect(Collectors.toList());

        return getMembersRes;
    }

    //GetMembersRes 객체를 생성
    private GetMembersRes createGetMembersRes(ProjectMember projectMember, Long memberId) {
        User user = projectMember.getUser();
        GetMembersRes getMembersRes = modelMapper.map(projectMember, GetMembersRes.class);
        getMembersRes.setUserId(user.getUserId());
        getMembersRes.setName(user.getName());
        getMembersRes.setImg(user.getImg());
        getMembersRes.setPosition(projectMember.getPosition());

        ProjectMember accessMember = projectMemberRepository.findById(memberId).orElseThrow(() -> new CustomException(NOT_EXISTS_PROJECT_MEMBER));

        char position = accessMember.getPosition();
        char deleteYN = (position == 'L') ? 'Y' : 'N';
        getMembersRes.setDeleteYN(deleteYN);

        return getMembersRes;
    }



    /**
     * 4.2 프로젝트 멤버 추가
     */

    /**
     * 4.3 프로젝트 멤버 제거
     */
    @Override
    @Transactional
    public String deleteMember(Long memberId) {
        //project member 정보
        ProjectMember projectMember = projectMemberRepository.findById(memberId).orElseThrow(() -> new CustomException(NOT_EXISTS_PROJECT_MEMBER));


        projectMemberRepository.deleteById(projectMember.getMemberId());
        String result = "프로젝트 멤버가 제거되었습니다.";
        return result;
    }

    /**
     * 4.4 프로젝트 멤버 탈퇴
     */
    @Override
    @Transactional
    public String withdrawMember(Long userId, Long projectId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(NOT_EXISTS_USER));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomException(NOT_EXISTS_PROJECT));

        //project member 찾기
        ProjectMember member = findProjectMember(user, project);
        //project member status = 'N'
        deactivateProjectMember(member);

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
