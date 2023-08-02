package com.momentum.releaser.domain.project.application;


import com.momentum.releaser.domain.project.dao.ProjectMemberRepository;
import com.momentum.releaser.domain.project.dao.ProjectRepository;
import com.momentum.releaser.domain.project.domain.Project;
import com.momentum.releaser.domain.project.domain.ProjectMember;
import com.momentum.releaser.domain.project.dto.ProjectMemberResponseDto;
import com.momentum.releaser.domain.project.dto.ProjectMemberResponseDto.InviteProjectMemberResponseDTO;
import com.momentum.releaser.domain.project.dto.ProjectMemberResponseDto.MembersResponseDTO;
import com.momentum.releaser.domain.release.dao.approval.ReleaseApprovalRepository;
import com.momentum.releaser.domain.release.dao.release.ReleaseRepository;
import com.momentum.releaser.domain.user.dao.UserRepository;
import com.momentum.releaser.domain.user.domain.User;
import com.momentum.releaser.global.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProjectMemberServiceImplTest {

    private ProjectMemberService projectMemberService;
    private ProjectMemberRepository projectMemberRepository;
    private ProjectRepository projectRepository;
    private UserRepository userRepository;
    private ReleaseApprovalRepository releaseApprovalRepository;
    private ReleaseRepository releaseRepository;

    @BeforeEach
    void setUp() {
        projectMemberRepository = mock(ProjectMemberRepository.class);
        projectRepository = mock(ProjectRepository.class);
        userRepository = mock(UserRepository.class);
        releaseApprovalRepository = mock(ReleaseApprovalRepository.class);
        releaseRepository = mock(ReleaseRepository.class);
        projectMemberService = new ProjectMemberServiceImpl(
                projectMemberRepository, projectRepository, userRepository, releaseApprovalRepository, releaseRepository);
    }

    @Test
    @DisplayName("4.1 프로젝트 멤버 조회")
    void testFindProjectMembers() {
        // Mock 데이터 설정
        Long mockProjectId = 1L;
        String mockEmail = "testUser@releaser.com";

        Project mockProject = new Project(
                "projectTitle",
                "projectContent",
                "projectTeam",
                "",
                "testlinktestlink",
                'Y'
        );
        User mockUser1 = new User(
                "testUserName",
                mockEmail,
                "",
                'Y'
        );
        User mockUser2 = new User(
                "testUserName2",
                "testUser2@releaser.com",
                "",
                'Y'
        );
        ProjectMember mockAccessMember = new ProjectMember(
                'L',
                'Y',
                mockUser1,
                mockProject
        );
        List<ProjectMember> projectMembers = new ArrayList<>();
        ProjectMember mockMember = new ProjectMember(
                'M',
                'Y',
                mockUser2,
                mockProject
        );
        projectMembers.add(mockAccessMember);
        projectMembers.add(mockMember);

        // projectRepository.findById() 메서드 동작 가짜 구현(Mock)
        when(projectRepository.findById(mockProjectId)).thenReturn(java.util.Optional.of(mockProject));

        // userRepository.findByEmail() 메서드 동작 가짜 구현(Mock)
        when(userRepository.findByEmail(mockEmail)).thenReturn(java.util.Optional.of(mockUser1));

        // projectMemberRepository.findByUserAndProject() 메서드 동작 가짜 구현(Mock)
        when(projectMemberRepository.findByUserAndProject(mockUser1, mockProject)).thenReturn(mockAccessMember);

        // projectMemberRepository.findByProject() 메서드 동작 가짜 구현(Mock)
        when(projectMemberRepository.findByProject(mockProject)).thenReturn(projectMembers);

        // 테스트할 메서드 실행
        MembersResponseDTO result = projectMemberService.findProjectMembers(mockProjectId, mockEmail);

        // 결과 검증
        assertNotNull(result);
        assertEquals(mockProject.getLink(), result.getLink());
        assertNotNull(result.getMemberList());
        assertEquals(2, result.getMemberList().size());
        assertEquals('Y', result.getMemberList().get(0).getDeleteYN());
        assertEquals('Y', result.getMemberList().get(1).getDeleteYN());

        // 필요한 메서드가 호출되었는지 검증
        verify(projectRepository, times(1)).findById(mockProjectId);
        verify(userRepository, times(1)).findByEmail(mockEmail);
        verify(projectMemberRepository, times(1)).findByUserAndProject(mockUser1, mockProject);
        verify(projectMemberRepository, times(1)).findByProject(mockProject);
    }

    @Test
    @DisplayName("4.2 프로젝트 멤버 추가 - 이미 존재하는 멤버를 추가하는 경우")
    void testAddProjectMember_AlreadyExists() {
        // Mock 데이터 설정
        String mockLink = "testLink";
        String mockEmail = "testUser@releaser.com";

        Project mockProject = new Project(
                "projectTitle",
                "projectContent",
                "projectTeam",
                "",
                mockLink,
                'Y'
        );
        User mockUser1 = new User(
                "testUserName",
                mockEmail,
                "",
                'Y'
        );
        ProjectMember mockAccessMember = new ProjectMember(
                'L',
                'Y',
                mockUser1,
                mockProject
        );

        // projectRepository.findByLink() 메서드 동작 가짜 구현(Mock)
        when(projectRepository.findByLink(mockLink)).thenReturn(java.util.Optional.of(mockProject));

        // userRepository.findByEmail() 메서드 동작 가짜 구현(Mock)
        when(userRepository.findByEmail(mockEmail)).thenReturn(java.util.Optional.of(mockUser1));

        // projectMemberRepository.findByUserAndProject() 메서드 동작 가짜 구현(Mock)
        when(projectMemberRepository.findByUserAndProject(mockUser1, mockProject)).thenReturn(mockAccessMember);

        // 테스트할 메서드 실행
        assertThrows(CustomException.class, () -> {
            projectMemberService.addProjectMember(mockLink, mockEmail);
        });

        // 필요한 메서드가 호출되었는지 검증
        verify(projectRepository, times(1)).findByLink(mockLink);
        verify(userRepository, times(1)).findByEmail(mockEmail);
        verify(projectMemberRepository, times(1)).findByUserAndProject(mockUser1, mockProject);
        verify(projectMemberRepository, never()).save(any(ProjectMember.class));
        verify(releaseRepository, never()).findAllByProject(mockProject);
        verify(releaseApprovalRepository, never()).saveAll(anyList());
    }

    @Test
    @DisplayName("4.2 프로젝트 멤버 추가 - 새로운 멤버를 추가하는 경우")
    void testAddProjectMember_NewMember() {
        // Mock 데이터 설정
        String mockLink = "testLink";
        String mockEmail = "testUser@releaser.com";

        Project mockProject = new Project(
                "projectTitle",
                "projectContent",
                "projectTeam",
                "",
                mockLink,
                'Y'
        );
        User mockUser1 = new User(
                "testUserName",
                mockEmail,
                "",
                'Y'
        );

        // projectRepository.findByLink() 메서드 동작 가짜 구현(Mock)
        when(projectRepository.findByLink(mockLink)).thenReturn(java.util.Optional.of(mockProject));

        // userRepository.findByEmail() 메서드 동작 가짜 구현(Mock)
        when(userRepository.findByEmail(mockEmail)).thenReturn(java.util.Optional.of(mockUser1));

        // projectMemberRepository.findByUserAndProject() 메서드 동작 가짜 구현(Mock)
        when(projectMemberRepository.findByUserAndProject(mockUser1, mockProject)).thenReturn(null);

        // 테스트할 메서드 실행
        InviteProjectMemberResponseDTO result = projectMemberService.addProjectMember(mockLink, mockEmail);

        // 결과 검증
        assertNotNull(result);
        assertEquals(mockProject.getProjectId(), result.getProjectId());
        assertEquals(mockProject.getTitle(), result.getProjectName());

        // 필요한 메서드가 호출되었는지 검증
        verify(projectRepository, times(1)).findByLink(mockLink);
        verify(userRepository, times(1)).findByEmail(mockEmail);
        verify(projectMemberRepository, times(1)).findByUserAndProject(mockUser1, mockProject);
        verify(projectMemberRepository, times(1)).save(any(ProjectMember.class));
        verify(releaseRepository, times(1)).findAllByProject(mockProject);
        verify(releaseApprovalRepository, times(1)).saveAll(anyList());
    }


}