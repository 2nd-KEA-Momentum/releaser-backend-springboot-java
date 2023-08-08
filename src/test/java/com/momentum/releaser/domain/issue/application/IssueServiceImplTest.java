package com.momentum.releaser.domain.issue.application;

import com.momentum.releaser.domain.issue.dao.IssueNumRepository;
import com.momentum.releaser.domain.issue.dao.IssueOpinionRepository;
import com.momentum.releaser.domain.issue.dao.IssueRepository;
import com.momentum.releaser.domain.issue.domain.Issue;
import com.momentum.releaser.domain.issue.domain.IssueNum;
import com.momentum.releaser.domain.issue.domain.LifeCycle;
import com.momentum.releaser.domain.issue.domain.Tag;
import com.momentum.releaser.domain.issue.dto.IssueRequestDto.IssueInfoRequestDTO;
import com.momentum.releaser.domain.issue.dto.IssueResponseDto.IssueIdResponseDTO;
import com.momentum.releaser.domain.issue.dto.IssueResponseDto.IssueModifyResponseDTO;
import com.momentum.releaser.domain.project.dao.ProjectMemberRepository;
import com.momentum.releaser.domain.project.dao.ProjectRepository;
import com.momentum.releaser.domain.project.domain.Project;
import com.momentum.releaser.domain.project.domain.ProjectMember;
import com.momentum.releaser.domain.release.dao.release.ReleaseRepository;
import com.momentum.releaser.domain.release.domain.ReleaseEnum;
import com.momentum.releaser.domain.release.domain.ReleaseNote;
import com.momentum.releaser.domain.user.dao.UserRepository;
import com.momentum.releaser.domain.user.domain.User;
import com.momentum.releaser.global.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.util.Optional;

import static com.momentum.releaser.global.config.BaseResponseStatus.CONNECTED_ISSUE_EXISTS;
import static com.momentum.releaser.global.config.BaseResponseStatus.CONNECTED_RELEASE_EXISTS;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class IssueServiceImplTest {

    private IssueServiceImpl issueService;
    private IssueRepository issueRepository;
    private IssueOpinionRepository issueOpinionRepository;
    private IssueNumRepository issueNumRepository;
    private ProjectRepository projectRepository;
    private ProjectMemberRepository projectMemberRepository;
    private UserRepository userRepository;
    private ReleaseRepository releaseRepository;

    @BeforeEach
    void setUp() {
        issueRepository = mock(IssueRepository.class);
        issueOpinionRepository = mock(IssueOpinionRepository.class);
        issueNumRepository = mock(IssueNumRepository.class);
        projectRepository = mock(ProjectRepository.class);
        projectMemberRepository = mock(ProjectMemberRepository.class);
        userRepository = mock(UserRepository.class);
        releaseRepository = mock(ReleaseRepository.class);
        issueService = new IssueServiceImpl(issueRepository, issueOpinionRepository, issueNumRepository, projectRepository,
                projectMemberRepository, userRepository, releaseRepository);
    }

    @Test
    @DisplayName("7.1 이슈 생성")
    void testAddIssue() {
        // Mock 데이터
        Long mockProjectId = 1L;
        Long mockMemberId = 1L;
        String mockUserEmail = "testLeader@releaser.com";

        Project mockProject = new Project(
                "projectTitle", "projectContent", "projectTeam",
                null, "testLink", 'Y'
        );

        User mockUser = new User(
                "testUser1Name", mockUserEmail, null, 'Y'
        );

        ProjectMember mockProjectMember = new ProjectMember(
                'L', 'Y', mockUser, mockProject
        );

        IssueInfoRequestDTO mockIssueInfoRequestDTO = new IssueInfoRequestDTO(
                "Test Issue Title", "Test Issue Content",
                Tag.NEW.toString(), Date.valueOf("2023-08-02"), mockMemberId
        );

        Issue mockSavedIssue = new Issue(
                1L,
                "Test Issue Title", "Test Issue Content", null,
                Tag.NEW, Date.valueOf("2023-08-02"), LifeCycle.DONE,
                'N', 'Y', mockProject, mockProjectMember, null, null
        );

        IssueNum mockIssueNum = new IssueNum(
                1L, mockSavedIssue, mockProject, 1L
        );

        mockSavedIssue.updateIssueNum(mockIssueNum);

        // projectRepository.findById() 메서드 동작 가짜 구현(Mock)
        when(projectRepository.findById(mockProjectId)).thenReturn(Optional.of(mockProject));

        // projectMemberRepository.findById() 메서드 동작 가짜 구현(Mock)
        when(projectMemberRepository.findById(mockMemberId)).thenReturn(Optional.of(mockProjectMember));

        // issueRepository.getIssueNum() 메서드 동작 가짜 구현(Mock)
        when(issueRepository.getIssueNum(mockProject)).thenReturn(0L);

        // issueRepository.save() 메서드 동작 가짜 구현(Mock)
        when(issueRepository.save(any(Issue.class))).thenReturn(mockSavedIssue);

        // 메서드 실행
        IssueIdResponseDTO result = issueService.addIssue(mockProjectId, mockIssueInfoRequestDTO);

        // 결과 검증
        assertNotNull(result);
        assertEquals(mockSavedIssue.getIssueId(), result.getIssueId());

        // 필요한 메서드가 호출되었는지 검증
        verify(projectRepository, times(1)).findById(mockProjectId);
        verify(projectMemberRepository, times(1)).findById(mockMemberId);
        verify(issueRepository, times(1)).getIssueNum(mockProject);
        verify(issueRepository, times(1)).save(any(Issue.class));
    }

    @Test
    @DisplayName("7.2 이슈 수정 - PM이 수정할 경우")
    void testModifyIssueWithPM() {
        //mock 데이터 설정
        Long mockIssueId = 1L;
        String mockAccessUserEmail = "test@releaser.com";
        User mockAccessUser = new User(
                "accessUser", mockAccessUserEmail, null, 'Y'
        );
        Project mockProject = new Project(
                "projectTitle", "projectContent", "projectTeam",
                null, "testLink", 'Y'
        );
        ProjectMember mockAccessMember = new ProjectMember(
                'L', 'Y', mockAccessUser, mockProject
        );
        ProjectMember mockManagerMember = new ProjectMember(
                'M', 'Y', null, mockProject
        );
        Issue mockIssue = new Issue(
                mockIssueId, "Issue Title", "Issue Content", null,
                Tag.CHANGED, null, LifeCycle.NOT_STARTED, 'N', 'Y',
                mockProject, mockManagerMember, null, null
        );
        IssueInfoRequestDTO mockReqDTO = new IssueInfoRequestDTO(
                "Update Issue Title", "Update Issue Content",
                String.valueOf(Tag.FEATURE), null, 2L
        );
        Issue mockUpdateIssue = new Issue(
                mockIssueId, "Update Issue Title", "Update Issue Content", null,
                Tag.FEATURE, null, LifeCycle.NOT_STARTED, 'N', 'Y',
                mockProject, mockManagerMember, null, null
        );

        when(issueRepository.findById(mockIssueId)).thenReturn(Optional.of(mockIssue));
        when(userRepository.findOneByEmail(mockAccessUserEmail)).thenReturn(Optional.of(mockAccessUser));
        when(projectMemberRepository.findByUserAndProject(mockAccessUser, mockProject)).thenReturn(Optional.of(mockAccessMember));
        when(projectMemberRepository.findById(2L)).thenReturn(Optional.of(mockManagerMember));
        when(issueRepository.save(any(Issue.class))).thenReturn(mockUpdateIssue);

        IssueModifyResponseDTO result = issueService.modifyIssue(mockIssueId, mockAccessUserEmail, mockReqDTO);

        assertNotNull(result);

        verify(issueRepository, times(1)).findById(mockIssueId);
        verify(userRepository, times(1)).findOneByEmail(mockAccessUserEmail);
        verify(projectMemberRepository, times(1)).findByUserAndProject(mockAccessUser, mockProject);
        verify(projectMemberRepository, times(1)).findById(2L);
        verify(issueRepository, times(1)).save(any(Issue.class));
    }

    @Test
    @DisplayName("7.2 이슈 수정 - member가 수정할 경우")
    void testModifyIssueWithMember() {
        //mock 데이터 설정
        Long mockIssueId = 1L;
        String mockAccessUserEmail = "test@releaser.com";
        User mockAccessUser = new User(
                "accessUser", mockAccessUserEmail, null, 'Y'
        );
        Project mockProject = new Project(
                "projectTitle", "projectContent", "projectTeam",
                null, "testLink", 'Y'
        );
        ProjectMember mockAccessMember = new ProjectMember(
                'M', 'Y', mockAccessUser, mockProject
        );
        ProjectMember mockManagerMember = new ProjectMember(
                'M', 'Y', null, mockProject
        );
        Issue mockIssue = new Issue(
                mockIssueId, "Issue Title", "Issue Content", null,
                Tag.CHANGED, null, LifeCycle.NOT_STARTED, 'N', 'Y',
                mockProject, mockManagerMember, null, null
        );
        IssueInfoRequestDTO mockReqDTO = new IssueInfoRequestDTO(
                "Update Issue Title", "Update Issue Content",
                String.valueOf(Tag.FEATURE), null, 2L
        );
        Issue mockUpdateIssue = new Issue(
                mockIssueId, "Update Issue Title", "Update Issue Content", null,
                Tag.FEATURE, null, LifeCycle.NOT_STARTED, 'Y', 'Y',
                mockProject, mockManagerMember, null, null
        );

        when(issueRepository.findById(mockIssueId)).thenReturn(Optional.of(mockIssue));
        when(userRepository.findOneByEmail(mockAccessUserEmail)).thenReturn(Optional.of(mockAccessUser));
        when(projectMemberRepository.findByUserAndProject(mockAccessUser, mockProject)).thenReturn(Optional.of(mockAccessMember));
        when(projectMemberRepository.findById(2L)).thenReturn(Optional.of(mockManagerMember));
        when(issueRepository.save(any(Issue.class))).thenReturn(mockUpdateIssue);

        IssueModifyResponseDTO result = issueService.modifyIssue(mockIssueId, mockAccessUserEmail, mockReqDTO);

        assertNotNull(result);

        verify(issueRepository, times(1)).findById(mockIssueId);
        verify(userRepository, times(1)).findOneByEmail(mockAccessUserEmail);
        verify(projectMemberRepository, times(1)).findByUserAndProject(mockAccessUser, mockProject);
        verify(projectMemberRepository, times(1)).findById(2L);
        verify(issueRepository, times(1)).save(any(Issue.class));
    }


    @Test
    @DisplayName("7.3 이슈 제거 - 연결된 릴리즈가 없는 경우")
    void testRemoveIssueWithoutConnectedRelease() {
        // Mock 데이터 설정
        Long mockIssueId = 1L;
        Project mockProject = new Project(
                "projectTitle", "projectContent", "projectTeam",
                null, "testLink", 'Y'
        );
        IssueNum mockIssueNum = new IssueNum(
                2L, null, mockProject, 2L
        );
        Issue mockIssue = new Issue(
                mockIssueId, "issueTitle", "issueContent",
                null, Tag.FIXED, null,
                LifeCycle.NOT_STARTED, 'N', 'Y', mockProject,
                null, null, null);
        mockIssue.updateIssueNum(mockIssueNum);

        // issueRepository.findById() 메서드 동작 가짜 구현(Mock)
        when(issueRepository.findById(mockIssueId)).thenReturn(Optional.of(mockIssue));

        // 메서드 실행
        String result = issueService.removeIssue(mockIssueId);

        // 결과 검증
        assertEquals("이슈가 삭제되었습니다.", result);

        // 필요한 메서드가 호출되었는지 검증
        verify(issueRepository, times(1)).findById(mockIssueId);
        verify(issueNumRepository, times(1)).deleteById(mockIssueNum.getIssueNumId());
        verify(issueRepository, times(1)).deleteById(mockIssueId);
    }

    @Test
    @DisplayName("7.3 이슈 제거 - 연결된 릴리즈가 있는 경우 예외 발생")
    void testRemoveIssueWithConnectedRelease() {
        // Mock 데이터 설정
        Long mockIssueId = 1L;
        Long mockReleaseId = 2L;
        Project mockProject = new Project(
                "projectTitle",
                "projectContent",
                "projectTeam",
                null,
                "testLink",
                'Y'
        );
        ReleaseNote mockRelease = new ReleaseNote(
                mockReleaseId,
                "releaseTitle", "releaseDescription",
                null,"1.0.0", null,
                ReleaseEnum.ReleaseDeployStatus.PLANNING,
                mockProject,50.0, 50.0);
        Issue mockIssue = new Issue(
                mockIssueId,
                "issueTitle", "issueContent",
                null, Tag.FIXED, null,
                LifeCycle.NOT_STARTED, 'N', 'Y',
                mockProject, null, mockRelease, null);

        // issueRepository.findById() 메서드 동작 가짜 구현(Mock)
        when(issueRepository.findById(mockIssueId)).thenReturn(Optional.of(mockIssue));

        // 예외 클래스와 메시지 검증용
        String expectedExceptionMessage = String.valueOf(CONNECTED_RELEASE_EXISTS);

        // 테스트 실행 및 예외 검증
        assertThrows(CustomException.class, () -> issueService.removeIssue(mockIssueId), expectedExceptionMessage);

        // 필요한 메서드가 호출되었는지 검증
        verify(issueRepository, times(1)).findById(mockIssueId);
        verify(issueNumRepository, never()).deleteById(anyLong());
        verify(issueRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("7.8 이슈 상태 변경 - 연결된 릴리즈가 없는 경우")
    void testModifyIssueLifeCycleWithoutConnectedIssue() {
        // Mock 데이터 설정
        Long mockIssueId = 1L;
        String mockLifeCycle = "IN_PROGRESS";
        Issue mockIssue = new Issue(
                mockIssueId,
                "issueTitle", "issueContent", null,
                Tag.FIXED, null, LifeCycle.NOT_STARTED,
                'N', 'Y', null, null, null, null);

        // issueRepository.findById() 메서드 동작 가짜 구현(Mock)
        when(issueRepository.findById(mockIssueId)).thenReturn(Optional.of(mockIssue));

        // 메서드 실행
        String result = issueService.modifyIssueLifeCycle(mockIssueId, mockLifeCycle);

        // 결과 검증
        assertEquals("이슈 상태 변경이 완료되었습니다.", result);

        // 필요한 메서드가 호출되었는지 검증
        verify(issueRepository, times(1)).findById(mockIssueId);
        verify(issueRepository, times(1)).save(any(Issue.class));
    }

    @Test
    @DisplayName("7.8 이슈 상태 변경 - 연결된 이슈가 있는 경우 예외 발생")
    void testModifyIssueLifeCycleWithConnectedIssue() {
        // Mock 데이터 설정
        Long mockIssueId = 1L;
        String mockLifeCycle = "IN_PROGRESS";
        Long mockReleaseId = 2L;
        ReleaseNote mockRelease = new ReleaseNote(
                mockReleaseId,
                "releaseTitle", "releaseDescription", null,
                "1.0.0", null, ReleaseEnum.ReleaseDeployStatus.PLANNING,
                null, 50.0, 50.0);
        Issue mockIssue = new Issue(mockIssueId, "issueTitle", "issueContent", null, Tag.FIXED, null,
                LifeCycle.NOT_STARTED, 'N', 'Y', null, null, mockRelease, null);

        // issueRepository.findById() 메서드 동작 가짜 구현(Mock)
        when(issueRepository.findById(mockIssueId)).thenReturn(Optional.of(mockIssue));

        // 예외 클래스와 메시지 검증용
        String expectedExceptionMessage = String.valueOf(CONNECTED_ISSUE_EXISTS);

        // 테스트 실행 및 예외 검증
        assertThrows(CustomException.class, () -> issueService.modifyIssueLifeCycle(mockIssueId, mockLifeCycle), expectedExceptionMessage);

        // 필요한 메서드가 호출되었는지 검증
        verify(issueRepository, times(1)).findById(mockIssueId);
        verify(issueRepository, never()).save(any(Issue.class));
    }
}