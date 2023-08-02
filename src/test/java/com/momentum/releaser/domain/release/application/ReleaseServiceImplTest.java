package com.momentum.releaser.domain.release.application;

import com.momentum.releaser.domain.issue.dao.IssueRepository;
import com.momentum.releaser.domain.issue.domain.Issue;
import com.momentum.releaser.domain.project.dao.ProjectMemberRepository;
import com.momentum.releaser.domain.project.dao.ProjectRepository;
import com.momentum.releaser.domain.project.domain.Project;
import com.momentum.releaser.domain.project.domain.ProjectMember;
import com.momentum.releaser.domain.release.dao.approval.ReleaseApprovalRepository;
import com.momentum.releaser.domain.release.dao.opinion.ReleaseOpinionRepository;
import com.momentum.releaser.domain.release.dao.release.ReleaseRepository;
import com.momentum.releaser.domain.release.domain.ReleaseEnum;
import com.momentum.releaser.domain.release.domain.ReleaseNote;
import com.momentum.releaser.domain.release.dto.ReleaseRequestDto;
import com.momentum.releaser.domain.release.dto.ReleaseRequestDto.ReleaseCreateRequestDTO;
import com.momentum.releaser.domain.release.dto.ReleaseResponseDto;
import com.momentum.releaser.domain.release.dto.ReleaseResponseDto.ReleaseCreateAndUpdateResponseDTO;
import com.momentum.releaser.domain.user.dao.UserRepository;
import com.momentum.releaser.domain.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReleaseServiceImplTest {

    private ReleaseService releaseService;
    private UserRepository userRepository;
    private ProjectRepository projectRepository;
    private ProjectMemberRepository projectMemberRepository;
    private ReleaseRepository releaseRepository;
    private ReleaseOpinionRepository releaseOpinionRepository;
    private ReleaseApprovalRepository releaseApprovalRepository;
    private IssueRepository issueRepository;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        projectRepository = mock(ProjectRepository.class);
        projectMemberRepository = mock(ProjectMemberRepository.class);
        releaseRepository = mock(ReleaseRepository.class);
        releaseOpinionRepository = mock(ReleaseOpinionRepository.class);
        releaseApprovalRepository = mock(ReleaseApprovalRepository.class);
        issueRepository = mock(IssueRepository.class);
        releaseService = new ReleaseServiceImpl(
                userRepository, projectRepository, projectMemberRepository, releaseRepository, releaseOpinionRepository, releaseApprovalRepository, issueRepository
        );
    }

//    @Test
//    @DisplayName("5.2 릴리즈 노트 생성 - 프로젝트 매니저가 릴리즈 노트를 추가하는 경우")
//    void testAddReleaseNoteByProjectManager() {
//        // Mock 데이터
//        Long projectId = 1L;
//        String userEmail = "testLeader@releaser.com";
//
//        Project mockProject = new Project(
//                "projectTitle",
//                "projectContent",
//                "projectTeam",
//                "",
//                "testLink",
//                'Y'
//        );
//        User mockUser = new User(
//                "testUserName",
//                userEmail,
//                "",
//                'Y'
//        );
//        ProjectMember mockProjectManager = new ProjectMember(
//                'L',
//                'Y',
//                mockUser,
//                mockProject
//        );
//
//        ReleaseCreateRequestDTO releaseCreateRequestDto = new ReleaseCreateRequestDTO(
//                "Test Release",
//                "MAJOR",
//                "Test Release Content",
//                "Test Release Summary",
//                50.0,
//                50.0,
//                List.of(1L, 2L)
//        );
//
//        ReleaseNote savedReleaseNote = new ReleaseNote(
//                1L,
//                "save Release Title",
//                "save Release Content",
//                "save Release Summary",
//                "1.0.0",
//                Date.valueOf("2023-08-02"),
//                ReleaseEnum.ReleaseDeployStatus.PLANNING,
//                mockProject,
//                50.0,
//                50.0
//        );
//
//        when(projectRepository.findById(projectId)).thenReturn(Optional.of(mockProject));
//        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(mockUser));
//        when(projectMemberRepository.findByUserAndProject(mockUser, mockProject)).thenReturn(mockProjectManager);
//        when(releaseRepository.save(any(ReleaseNote.class))).thenReturn(savedReleaseNote);
//
//        // 메서드 실행
//        ReleaseCreateAndUpdateResponseDTO result = releaseService.addReleaseNote(userEmail, projectId, releaseCreateRequestDto);
//
//        // 결과 검증
//        assertNotNull(result);
//        assertEquals(savedReleaseNote.getReleaseId(), result.getReleaseId());
//        assertEquals(savedReleaseNote.getVersion(), result.getVersion());
//        assertEquals(savedReleaseNote.getSummary(), result.getSummary());
//        assertEquals(savedReleaseNote.getCoordX(), result.getCoordX());
//        assertEquals(savedReleaseNote.getCoordY(), result.getCoordY());
//
//        // 필요한 메서드가 호출되었는지 검증
//        verify(projectRepository, times(1)).findById(projectId);
//        verify(userRepository, times(1)).findByEmail(userEmail);
//        verify(projectMemberRepository, times(1)).findByUserAndProject(mockUser, mockProject);
//        verify(releaseRepository, times(1)).save(any(ReleaseNote.class));
//        verify(issueRepository, times(2)).findById(anyLong()); // 리스트에 두 개의 이슈가 있으므로 2번 호출
//        verify(issueRepository, times(2)).save(any(Issue.class)); // 두 개의 이슈가 릴리즈 노트로 업데이트되므로 2번 호출
//        verify(releaseApprovalRepository, times(1)).saveAll(anyList()); // 각 멤버당 하나씩의 릴리즈 동의 테이블이 생성되므로 1번 호출
//    }



}