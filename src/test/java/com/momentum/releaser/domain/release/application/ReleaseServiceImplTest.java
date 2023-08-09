package com.momentum.releaser.domain.release.application;

import com.momentum.releaser.domain.issue.dao.IssueRepository;
import com.momentum.releaser.domain.issue.domain.Issue;
import com.momentum.releaser.domain.issue.domain.IssueNum;
import com.momentum.releaser.domain.issue.domain.LifeCycle;
import com.momentum.releaser.domain.issue.domain.Tag;
import com.momentum.releaser.domain.project.dao.ProjectMemberRepository;
import com.momentum.releaser.domain.project.dao.ProjectRepository;
import com.momentum.releaser.domain.project.domain.Project;
import com.momentum.releaser.domain.project.domain.ProjectMember;
import com.momentum.releaser.domain.release.dao.approval.ReleaseApprovalRepository;
import com.momentum.releaser.domain.release.dao.opinion.ReleaseOpinionRepository;
import com.momentum.releaser.domain.release.dao.release.ReleaseRepository;
import com.momentum.releaser.domain.release.domain.ReleaseApproval;
import com.momentum.releaser.domain.release.domain.ReleaseEnum;
import com.momentum.releaser.domain.release.domain.ReleaseNote;
import com.momentum.releaser.domain.release.dto.ReleaseRequestDto;
import com.momentum.releaser.domain.release.dto.ReleaseRequestDto.ReleaseCreateRequestDTO;
import com.momentum.releaser.domain.release.dto.ReleaseResponseDto;
import com.momentum.releaser.domain.release.dto.ReleaseResponseDto.ReleaseCreateAndUpdateResponseDTO;
import com.momentum.releaser.domain.user.dao.UserRepository;
import com.momentum.releaser.domain.user.domain.User;
import com.momentum.releaser.global.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.momentum.releaser.global.config.BaseResponseStatus.INVALID_RELEASE_VERSION_TYPE;
import static com.momentum.releaser.global.config.BaseResponseStatus.NOT_EXISTS_ISSUE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReleaseServiceImplTest {

    private ReleaseServiceImpl releaseService;
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

    @Test
    @DisplayName("5.2 릴리즈 노트 생성 - pm만 릴리즈 노트 생성 가능")
    void testAddReleaseNote_ByPM() {
        // Mock 데이터
        Long mockProjectId = 1L;
        String userEmail = "testLeader@releaser.com";

        Project mockProject = new Project(
                mockProjectId, "projectTitle", "projectContent", "projectTeam",
                null, "testLink", 'Y'
        );
        User mockUser1 = new User(
                "testUser1Name", userEmail, null, 'Y'
        );
        User mockUser2 = new User(
                "testUser2Name", "testMember@releaser.com", null, 'Y'
        );
        ProjectMember mockLeaderMember = new ProjectMember(
                1L, 'L', 'Y', mockUser1, mockProject
        );
        ProjectMember mockMember = new ProjectMember(
                2L, 'M', 'Y', mockUser2, mockProject
        );
        List<ProjectMember> mockMemberList = List.of(mockLeaderMember, mockMember);
        Issue mockIssue1 = new Issue(
                1L, "Test Issue Title", "Test Issue Content", null,
                Tag.NEW, Date.valueOf("2023-08-02"), LifeCycle.DONE, 'N', 'Y',
                mockProject, mockLeaderMember, null, null
        );
        Issue mockIssue2 = new Issue(
                2L, "Test Issue Title", "Test Issue Content", null,
                Tag.NEW, Date.valueOf("2023-08-02"), LifeCycle.DONE, 'N', 'Y',
                mockProject, mockLeaderMember, null, null
        );
        ReleaseCreateRequestDTO mockReleaseCreateRequestDto = new ReleaseCreateRequestDTO(
                "Test Release", "MAJOR", "Test Release Content", "Test Release Summary",
                50.0, 50.0, List.of(1L, 2L)
        );
        ReleaseNote mockSavedReleaseNote = new ReleaseNote(
                1L, "save Release Title", "save Release Content", "save Release Summary", "1.0.0",
                Date.valueOf("2023-08-02"), ReleaseEnum.ReleaseDeployStatus.PLANNING, mockProject, 50.0, 50.0
        );
        when(projectRepository.findById(mockProjectId)).thenReturn(Optional.of(mockProject));
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(mockUser1));
        when(projectMemberRepository.findByUserAndProject(mockUser1, mockProject)).thenReturn(Optional.of(mockLeaderMember));
        when(issueRepository.findById(mockIssue1.getIssueId())).thenReturn(Optional.of(mockIssue1));
        when(issueRepository.findById(mockIssue2.getIssueId())).thenReturn(Optional.of(mockIssue2));
        when(releaseRepository.save(any(ReleaseNote.class))).thenReturn(mockSavedReleaseNote);
        when(projectMemberRepository.findByProject(mockProject)).thenReturn(mockMemberList);

        // 메서드 실행
        ReleaseCreateAndUpdateResponseDTO result = releaseService.addReleaseNote(userEmail, mockProjectId, mockReleaseCreateRequestDto);

        // 결과 검증
        assertNotNull(result);
        assertEquals(mockSavedReleaseNote.getReleaseId(), result.getReleaseId());
        assertEquals(mockSavedReleaseNote.getVersion(), result.getVersion());
        assertEquals(mockSavedReleaseNote.getSummary(), result.getSummary());
        assertEquals(mockSavedReleaseNote.getCoordX(), result.getCoordX());
        assertEquals(mockSavedReleaseNote.getCoordY(), result.getCoordY());

        // 필요한 메서드가 호출되었는지 검증
        verify(projectRepository, times(1)).findById(mockProjectId);
        verify(userRepository, times(1)).findByEmail(userEmail);
        verify(projectMemberRepository, times(1)).findByUserAndProject(mockUser1, mockProject);
        verify(issueRepository, times(2)).findById(anyLong()); // 리스트에 두 개의 이슈가 있으므로 2번 호출
        verify(releaseRepository, times(1)).save(any(ReleaseNote.class));
        verify(releaseApprovalRepository, times(mockMemberList.size())).save(any(ReleaseApproval.class));

    }

    @Test
    @DisplayName("5.2 릴리즈 노트 생성 - 다음 버전을 알맞게 추가한 경우")
    void testAddReleaseNote_ValidVersion() {
        // Mock 데이터
        Project mockProject = new Project(
                1L, "projectTitle", "projectContent", "projectTeam",
                null, "testLink", 'Y'
        );

        // releaseRepository가 빈 리스트를 반환하도록 설정
        List<String> mockReleaseVersions = new ArrayList<>();
        mockReleaseVersions.add("1.0.0");
        when(releaseRepository.findAllVersionsByProject(mockProject)).thenReturn(mockReleaseVersions);

        // 테스트할 메서드 호출
        String newVersion = releaseService.createReleaseVersion(mockProject, "MAJOR");

        // 테스트 결과 검증
        assertEquals("2.0.0", newVersion);

        // 필요한 메서드가 호출되었는지 검증
        verify(releaseRepository, times(1)).findAllVersionsByProject(mockProject);
    }

}