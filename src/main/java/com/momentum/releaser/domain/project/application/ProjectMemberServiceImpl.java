package com.momentum.releaser.domain.project.application;

import com.momentum.releaser.domain.project.dao.ProjectMemberRepository;
import com.momentum.releaser.domain.project.dao.ProjectRepository;
import com.momentum.releaser.domain.project.domain.Project;
import com.momentum.releaser.domain.project.domain.ProjectMember;
import com.momentum.releaser.domain.project.dto.ProjectResDto;
import com.momentum.releaser.domain.project.dto.ProjectResDto.GetMembersRes;
import com.momentum.releaser.domain.user.dao.UserRepository;
import com.momentum.releaser.global.config.BaseResponseStatus;
import com.momentum.releaser.global.error.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.momentum.releaser.global.config.BaseResponseStatus.NOT_EXISTS_PROJECT;
import static com.momentum.releaser.global.config.BaseResponseStatus.NOT_EXISTS_PROJECT_MEMBER;

@Slf4j
@Service
//final 있는 필드만 생성자 만들어줌
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
    public List<GetMembersRes> getMembers(Long projectId) {
        //project 정보
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new CustomException(NOT_EXISTS_PROJECT));

        //project member 정보
        List<ProjectMember> member = projectMemberRepository.findByProject(project);

        //Response : project member 조회
        List<GetMembersRes> getMembersRes = new ArrayList<>();

        for (ProjectMember projectMember : member) {
            getMembersRes.add(GetMembersRes.builder()
                    .memberId(projectMember.getMemberId())
                    .userId(projectMember.getUser().getUserId())
                    .name(projectMember.getUser().getName())
                    .img(projectMember.getUser().getImg())
                    .position(projectMember.getPosition())
                    .build());
        }

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
}
