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
import com.momentum.releaser.domain.release.domain.ReleaseEnum.ReleaseDeployStatus;
import com.momentum.releaser.domain.release.domain.ReleaseNote;
import com.momentum.releaser.domain.release.domain.ReleaseOpinion;
import com.momentum.releaser.domain.release.dto.ReleaseRequestDto;
import com.momentum.releaser.domain.release.dto.ReleaseRequestDto.ReleaseApprovalRequestDTO;
import com.momentum.releaser.domain.release.dto.ReleaseRequestDto.ReleaseCreateRequestDTO;
import com.momentum.releaser.domain.release.dto.ReleaseRequestDto.ReleaseUpdateRequestDTO;
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

import static com.momentum.releaser.global.config.BaseResponseStatus.*;
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

    @Test
    @DisplayName("5.3 릴리즈 노트 수정 - PM이 아닌 멤버가 수정 시 불가능")
    void testSaveReleaseWithoutPM() {
        String mockUserEmail = "testMember@releaser.com";
        Long mockReleaseId = 1L;

        User mockMemberUser = new User(
                "testUserName", mockUserEmail, null, 'Y'
        );
        Project mockProject = new Project(
                1L, "projectTitle", "projectContent", "projectTeam", null, "testLink", 'Y'
        );
        ProjectMember mockMember = new ProjectMember(
                1L, 'M', 'Y', mockMemberUser, mockProject
        );
        ReleaseNote mockRelease = new ReleaseNote(
                1L, "release Title", "release Content", null,
                "1.0.0", null, ReleaseDeployStatus.PLANNING, mockProject, 50.0, 50.0
        );
        ReleaseUpdateRequestDTO mockReqDTO = new ReleaseUpdateRequestDTO(
                "release Update Title", "2.0.0", "release Update Content",
                null, "PLANNING", null
        );

        when(releaseRepository.findById(mockReleaseId)).thenReturn(Optional.of(mockRelease));
        when(userRepository.findByEmail(mockUserEmail)).thenReturn(Optional.of(mockMemberUser));
        when(projectMemberRepository.findByUserAndProject(mockMemberUser, mockProject)).thenReturn(Optional.of(mockMember));

        // 예외 메시지 검증용
        String expectedExceptionMessage = String.valueOf(NOT_PROJECT_MANAGER);

        // 테스트 실행 및 예외 검증
        assertThrows(CustomException.class, () -> releaseService.saveReleaseNote(mockUserEmail, mockReleaseId, mockReqDTO), expectedExceptionMessage);

        verify(releaseRepository, times(1)).findById(mockReleaseId);
        verify(userRepository, times(1)).findByEmail(mockUserEmail);
        verify(projectMemberRepository, times(1)).findByUserAndProject(mockMemberUser, mockProject);
    }

    @Test
    @DisplayName("5.3 릴리즈 노트 수정 - 수정하려는 버전이 1.0.0인 경우 예외 발생")
    void testSaveReleaseWithVersion1_0_0() {
        String mockLeaderUserEmail = "testLeader@releaser.com";
        Long mockReleaseId = 1L;

        User mockLeaderUser = new User(
                "testUserName", mockLeaderUserEmail, null, 'Y'
        );
        Project mockProject = new Project(
                1L, "projectTitle", "projectContent", "projectTeam", null, "testLink", 'Y'
        );
        ProjectMember mockMember = new ProjectMember(
                1L, 'M', 'Y', mockLeaderUser, mockProject
        );
        ReleaseNote mockRelease = new ReleaseNote(
                mockReleaseId, "release Title", "release Content", null,
                "1.0.0", null, ReleaseDeployStatus.PLANNING, mockProject, 50.0, 50.0
        );
        ReleaseUpdateRequestDTO mockReqDTO = new ReleaseUpdateRequestDTO(
                "release Update Title", "2.0.0", "release Update Content",
                null, "PLANNING", null
        );

        when(releaseRepository.findById(mockReleaseId)).thenReturn(Optional.of(mockRelease));
        when(userRepository.findByEmail(mockLeaderUserEmail)).thenReturn(Optional.of(mockLeaderUser));
        when(projectMemberRepository.findByUserAndProject(mockLeaderUser, mockProject)).thenReturn(Optional.of(mockMember));

        // 예외를 확인하기 위해 assertThrows 사용
        String expectedExceptionMessage = String.valueOf(FAILED_TO_UPDATE_INITIAL_RELEASE_VERSION);

        assertThrows(CustomException.class, () -> releaseService.updateReleaseVersion(mockRelease, mockReqDTO.getVersion()), expectedExceptionMessage);

    }

    @Test
    @DisplayName("5.3 릴리즈 노트 수정 - 중복 버전인 경우 예외 발생")
    void testSaveReleaseWithDuplicatedVersion() {
        String mockLeaderUserEmail = "testLeader@releaser.com";
        Long mockReleaseId = 1L;

        User mockLeaderUser = new User(
                "testUserName", mockLeaderUserEmail, null, 'Y'
        );
        Project mockProject = new Project(
                1L, "projectTitle", "projectContent", "projectTeam", null, "testLink", 'Y'
        );
        ProjectMember mockMember = new ProjectMember(
                1L, 'M', 'Y', mockLeaderUser, mockProject
        );
        ReleaseNote mockRelease = new ReleaseNote(
                mockReleaseId, "release Title", "release Content", null,
                "1.0.1", null, ReleaseDeployStatus.PLANNING, mockProject, 50.0, 50.0
        );
        ReleaseUpdateRequestDTO mockReqDTO = new ReleaseUpdateRequestDTO(
                "release Update Title", "1.0.3", "release Update Content",
                null, "PLANNING", null
        );

        when(releaseRepository.findById(mockReleaseId)).thenReturn(Optional.of(mockRelease));
        when(userRepository.findByEmail(mockLeaderUserEmail)).thenReturn(Optional.of(mockLeaderUser));
        when(projectMemberRepository.findByUserAndProject(mockLeaderUser, mockProject)).thenReturn(Optional.of(mockMember));
        when(releaseRepository.existsByProjectAndVersion(eq(mockProject), eq(mockReleaseId), eq(mockReqDTO.getVersion()))).thenReturn(true);


        // 예외를 확인하기 위해 assertThrows 사용
        String expectedExceptionMessage = String.valueOf(DUPLICATED_RELEASE_VERSION);

        assertThrows(CustomException.class, () -> releaseService.updateReleaseVersion(mockRelease, mockReqDTO.getVersion()), expectedExceptionMessage);

    }

    @Test
    @DisplayName("5.3 릴리즈 노트 수정 - 잘못된 버전을 입력한 경우 예외 발생")
    void testSaveReleaseWithWrongVersion() {
        String mockLeaderUserEmail = "testLeader@releaser.com";
        Long mockReleaseId = 1L;

        User mockLeaderUser = new User(
                "testUserName", mockLeaderUserEmail, null, 'Y'
        );
        Project mockProject = new Project(
                1L, "projectTitle", "projectContent", "projectTeam", null, "testLink", 'Y'
        );
        ProjectMember mockMember = new ProjectMember(
                1L, 'M', 'Y', mockLeaderUser, mockProject
        );
        ReleaseNote mockRelease = new ReleaseNote(
                mockReleaseId, "release Title", "release Content", null,
                "1.0.0", null, ReleaseDeployStatus.PLANNING, mockProject, 50.0, 50.0
        );
        ReleaseUpdateRequestDTO mockReqDTO = new ReleaseUpdateRequestDTO(
                "release Update Title", "4.0.0", "release Update Content",
                null, "PLANNING", null
        );

        when(releaseRepository.findById(mockReleaseId)).thenReturn(Optional.of(mockRelease));
        when(userRepository.findByEmail(mockLeaderUserEmail)).thenReturn(Optional.of(mockLeaderUser));
        when(projectMemberRepository.findByUserAndProject(mockLeaderUser, mockProject)).thenReturn(Optional.of(mockMember));

        int[] majors = {1, 4};
        int[] minors = {0, 0};
        int[] patches = {0, 0};

        int end = majors.length - 1;
        int majorStartIdx = 0;
        int minorStartIdx = 0;

        String expectedExceptionMessage = String.valueOf(INVALID_RELEASE_VERSION);

        assertThrows(CustomException.class, () -> {
            releaseService.validateMajorVersion(majors, minors, patches, end, majorStartIdx, minorStartIdx);
        }, expectedExceptionMessage);

    }

    @Test
    @DisplayName("5.4 릴리즈 노트 삭제 - PM이 아닌 멤버가 삭제할 경우")
    void testRemoveReleaseNoteWithoutPM() {
        String mockUserEmail = "testMember@releaser.com";
        Long mockReleaseId = 1L;

        User mockMemberUser = new User(
                "memberUserName", mockUserEmail, null, 'Y'
        );
        Project mockProject = new Project(
                1L, "projectTitle", "projectContent", "projectTeam", null, "testLink", 'Y'
        );
        ProjectMember mockMember = new ProjectMember(
                1L, 'M', 'Y', mockMemberUser, mockProject
        );
        ReleaseNote mockRelease = new ReleaseNote(
                mockReleaseId, "release Title", "release Content", null,
                "1.0.0", null, ReleaseDeployStatus.PLANNING, mockProject, 50.0, 50.0
        );

        when(releaseRepository.findById(mockReleaseId)).thenReturn(Optional.of(mockRelease));
        when(userRepository.findByEmail(mockUserEmail)).thenReturn(Optional.of(mockMemberUser));
        when(projectMemberRepository.findByUserAndProject(mockMemberUser, mockProject)).thenReturn(Optional.of(mockMember));

        String expectedExceptionMessage = String.valueOf(NOT_PROJECT_MANAGER);

        assertThrows(CustomException.class, () -> {
            releaseService.removeReleaseNote(mockUserEmail, mockReleaseId);
        }, expectedExceptionMessage);

    }

    @Test
    @DisplayName("5.4 릴리즈 노트 삭제 - 배포된 릴리즈 노트일 경우 삭제 불가능")
    void testRemoveReleaseNoteWithDeployedRelease() {
        String mockUserEmail = "testLeader@releaser.com";
        Long mockReleaseId = 1L;

        User mockLeaderUser = new User(
                "leaderUserName", mockUserEmail, null, 'Y'
        );
        Project mockProject = new Project(
                1L, "projectTitle", "projectContent", "projectTeam", null, "testLink", 'Y'
        );
        ProjectMember mockMember = new ProjectMember(
                1L, 'M', 'Y', mockLeaderUser, mockProject
        );
        ReleaseNote mockRelease = new ReleaseNote(
                mockReleaseId, "release Title", "release Content", null,
                "1.0.0", null, ReleaseDeployStatus.DEPLOYED, mockProject, 50.0, 50.0
        );

        when(releaseRepository.findById(mockReleaseId)).thenReturn(Optional.of(mockRelease));
        when(userRepository.findByEmail(mockUserEmail)).thenReturn(Optional.of(mockLeaderUser));
        when(projectMemberRepository.findByUserAndProject(mockLeaderUser, mockProject)).thenReturn(Optional.of(mockMember));

        String expectedExceptionMessage = String.valueOf(FAILED_TO_DELETE_DEPLOYED_RELEASE_NOTE);

        assertThrows(CustomException.class, () -> {
            releaseService.removeReleaseNote(mockUserEmail, mockReleaseId);
        }, expectedExceptionMessage);

    }

    @Test
    @DisplayName("5.6 릴리즈 노트 배포 동의 여부 선택 - 프로젝트 멤버가 아닌 경우")
    void testModifyReleaseApprovalWithoutMember() {
        String mockUserEmail = "test@releaser.com";
        Long mockReleaseId = 1L;

        User mockUser = new User(
                "UserName", mockUserEmail, null, 'Y'
        );
        Project mockProject = new Project(
                1L, "projectTitle", "projectContent", "projectTeam", null, "testLink", 'Y'
        );

        ReleaseNote mockRelease = new ReleaseNote(
                mockReleaseId, "release Title", "release Content", null,
                "1.0.0", null, ReleaseDeployStatus.PLANNING, mockProject, 50.0, 50.0
        );
        ReleaseApprovalRequestDTO mockReqDTO = new ReleaseApprovalRequestDTO(
                "Y"
        );

        when(releaseRepository.findById(mockReleaseId)).thenReturn(Optional.of(mockRelease));
        when(userRepository.findByEmail(mockUserEmail)).thenReturn(Optional.of(mockUser));

        String expectedExceptionMessage = String.valueOf(NOT_EXISTS_PROJECT_MEMBER);

        assertThrows(CustomException.class, () -> {
            releaseService.modifyReleaseApproval(mockUserEmail, mockReleaseId, mockReqDTO);
        }, expectedExceptionMessage);

    }

    @Test
    @DisplayName("5.6 릴리즈 노트 배포 동의 여부 선택 - 배포된 릴리즈 노트인 경우 예외 발생")
    void testModifyReleaseApproval() {
        String mockMemberUserEmail = "testMember@releaser.com";
        Long mockReleaseId = 1L;

        User mockMemberUser = new User(
                "UserName", mockMemberUserEmail, null, 'Y'
        );
        Project mockProject = new Project(
                1L, "projectTitle", "projectContent", "projectTeam", null, "testLink", 'Y'
        );
        ProjectMember mockMember = new ProjectMember(
                1L, 'M', 'Y', mockMemberUser, mockProject
        );
        ReleaseNote mockRelease = new ReleaseNote(
                mockReleaseId, "release Title", "release Content", null,
                "1.0.0", null, ReleaseDeployStatus.DEPLOYED, mockProject, 50.0, 50.0
        );
        ReleaseApprovalRequestDTO mockReqDTO = new ReleaseApprovalRequestDTO(
                "Y"
        );

        when(releaseRepository.findById(mockReleaseId)).thenReturn(Optional.of(mockRelease));
        when(userRepository.findByEmail(mockMemberUserEmail)).thenReturn(Optional.of(mockMemberUser));
        when(projectMemberRepository.findByUserAndProject(mockMemberUser, mockProject)).thenReturn(Optional.of(mockMember));

        String expectedExceptionMessage = String.valueOf(FAILED_TO_APPROVE_RELEASE_NOTE);

        assertThrows(CustomException.class, () -> {
            releaseService.modifyReleaseApproval(mockMemberUserEmail, mockReleaseId, mockReqDTO);
        }, expectedExceptionMessage);

    }

    @Test
    @DisplayName("6.2 릴리즈 노트 의견 삭제 - 해당 의견 작성자가 아닌 경우 예외 발생")
    void testRemoveReleaseOpinionWithoutCommenter() {
        String mockUserEmail = "test@releaser.com";
        Long mockOpinionId = 1L;

        User mockMemberUser = new User(
                "UserName", mockUserEmail, null, 'Y'
        );
        User mockCommenterUser = new User(
                "CommenterUserName", "testCommenter@releaser.com", null, 'Y'
        );
        Project mockProject = new Project(
                1L, "projectTitle", "projectContent", "projectTeam", null, "testLink", 'Y'
        );
        ProjectMember mockMember = new ProjectMember(
                1L, 'M', 'Y', mockMemberUser, mockProject
        );
        ProjectMember mockCommenterMember = new ProjectMember(
                1L, 'M', 'Y', mockCommenterUser, mockProject
        );
        ReleaseNote mockRelease = new ReleaseNote(
                1L, "release Title", "release Content", null,
                "1.0.0", null, ReleaseDeployStatus.PLANNING, mockProject, 50.0, 50.0
        );
        ReleaseOpinion mockReleaseOpinion = new ReleaseOpinion(
                "opinion", mockRelease, mockCommenterMember
        );

        when(releaseOpinionRepository.findById(mockOpinionId)).thenReturn(Optional.of(mockReleaseOpinion));
        when(userRepository.findByEmail(mockUserEmail)).thenReturn(Optional.of(mockMemberUser));
        when(projectMemberRepository.findByUserAndProject(mockMemberUser, mockProject)).thenReturn(Optional.of(mockMember));

        String expectedExceptionMessage = String.valueOf(UNAUTHORIZED_TO_DELETE_RELEASE_OPINION);

        assertThrows(CustomException.class, () -> {
            releaseService.removeReleaseOpinion(mockUserEmail, mockOpinionId);
        }, expectedExceptionMessage);
    }


}