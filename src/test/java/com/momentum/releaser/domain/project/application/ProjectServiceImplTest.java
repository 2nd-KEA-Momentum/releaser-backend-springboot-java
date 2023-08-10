package com.momentum.releaser.domain.project.application;

import com.momentum.releaser.domain.issue.dao.IssueRepository;
import com.momentum.releaser.domain.project.dao.ProjectMemberRepository;
import com.momentum.releaser.domain.project.dao.ProjectRepository;
import com.momentum.releaser.domain.project.domain.Project;
import com.momentum.releaser.domain.project.domain.ProjectMember;
import com.momentum.releaser.domain.project.dto.ProjectDataDto.GetProjectDataDTO;
import com.momentum.releaser.domain.project.dto.ProjectRequestDto.FilterIssueRequestDTO;
import com.momentum.releaser.domain.project.dto.ProjectRequestDto.FilterReleaseRequestDTO;
import com.momentum.releaser.domain.project.dto.ProjectRequestDto.ProjectInfoRequestDTO;
import com.momentum.releaser.domain.project.dto.ProjectResponseDto.GetProjectResponseDTO;
import com.momentum.releaser.domain.project.dto.ProjectResponseDto.ProjectInfoResponseDTO;
import com.momentum.releaser.domain.project.dto.ProjectResponseDto.ProjectSearchResponseDTO;
import com.momentum.releaser.domain.release.dao.approval.ReleaseApprovalRepository;
import com.momentum.releaser.domain.release.dao.release.ReleaseRepository;
import com.momentum.releaser.domain.user.dao.UserRepository;
import com.momentum.releaser.domain.user.domain.User;
import com.momentum.releaser.global.config.aws.S3Upload;
import com.momentum.releaser.global.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.momentum.releaser.global.config.BaseResponseStatus.NOT_PROJECT_PM;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProjectServiceImplTest {

    private ProjectService projectService;
    private ProjectRepository projectRepository;
    private ProjectMemberRepository projectMemberRepository;
    private UserRepository userRepository;
    private IssueRepository issueRepository;
    private ReleaseRepository releaseRepository;
    private ReleaseApprovalRepository releaseApprovalRepository;
    private ModelMapper modelMapper;
    private S3Upload s3Upload;

    @BeforeEach
    void setUp() {
        projectRepository = mock(ProjectRepository.class);
        issueRepository = mock(IssueRepository.class);
        releaseRepository = mock(ReleaseRepository.class);
        releaseApprovalRepository = mock(ReleaseApprovalRepository.class);
        projectMemberRepository = mock(ProjectMemberRepository.class);
        userRepository = mock(UserRepository.class);
        modelMapper = new ModelMapper(); // modelMapper 초기화
        s3Upload = mock(S3Upload.class);
//        projectService = new ProjectServiceImpl(projectRepository, projectMemberRepository, userRepository, issueRepository, releaseRepository, releaseApprovalRepository, modelMapper, s3Upload);
    }

    @Test
    @DisplayName("3.1 프로젝트 생성")
    void testAddProject() throws IOException {
        //Mock 데이터 설정
        String mockUserEmail = "test@releaser.com";

        User mockUser = new User(
                "userName", mockUserEmail, null, 'Y'
        );
        ProjectInfoRequestDTO mockReqDTO = new ProjectInfoRequestDTO(
                "project Title", "project Content", "project Team", null
        );
        Project mockProject = new Project(
                1L, "project Title", "project Content", "project Team", "s3Url", "testLink", 'Y'
        );

        when(userRepository.findByEmail(mockUserEmail)).thenReturn(Optional.of(mockUser));
        when(s3Upload.upload(any(), anyString(), anyString())).thenReturn("s3Url");
        when(projectRepository.save(any())).thenReturn(mockProject);

        // When
        ProjectInfoResponseDTO result = projectService.addProject(mockUserEmail, mockReqDTO);

        // Then
        assertNotNull(result);

        verify(userRepository, times(1)).findByEmail(mockUserEmail);
        verify(projectRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("3.2 프로젝트 수정 - 프로젝트 PM이 수정한 경우")
    void testModifyProjectWithPM() throws IOException {
        Long mockProjectId = 1L;
        String mockUserEmail = "test@releaser.com";

        ProjectInfoRequestDTO mockReqDTO = new ProjectInfoRequestDTO(
                "project Update Title", "project Update Content", "project Team", null
        );
        User mockUser = new User(
                "pmUserName", mockUserEmail, null, 'Y'
        );
        Project mockProject = new Project(
                mockProjectId, "project Title", "project Content", "project Team", "s3Url", "testLink", 'Y'
        );
        ProjectMember mockPM = new ProjectMember(
                1L, 'L', 'Y', mockUser, mockProject
        );
        List<ProjectMember> memberList = new ArrayList<>();
        memberList.add(mockPM);

        when(projectRepository.findById(mockProjectId)).thenReturn(Optional.of(mockProject));
        when(userRepository.findByEmail(mockUserEmail)).thenReturn(Optional.of(mockUser));
        when(projectMemberRepository.findByProject(mockProject)).thenReturn(memberList);
        when(s3Upload.upload(any(), anyString(), anyString())).thenReturn("s3Url");
        when(projectRepository.save(any())).thenReturn(mockProject);

        ProjectInfoResponseDTO result = projectService.modifyProject(mockProjectId, mockUserEmail, mockReqDTO);

        assertNotNull(result);

        verify(projectRepository, times(1)).findById(mockProjectId);
        verify(userRepository, times(1)).findByEmail(mockUserEmail);
        verify(projectMemberRepository, times(1)).findByProject(mockProject);
        verify(projectRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("3.2 프로젝트 수정 - PM이 아닌 다른 멤버가 수정한 경우")
    void testModifyProjectWithoutPM() throws IOException {
        Long mockProjectId = 1L;
        String mockUserEmail = "testMember@releaser.com";

        ProjectInfoRequestDTO mockReqDTO = new ProjectInfoRequestDTO(
                "project Update Title", "project Update Content", "project Team", null
        );
        User mockLeaderUser = new User(
                "pmUserName", "testLeader@releaser.com", null, 'Y'
        );
        User mockMemberUser = new User(
                "memberUserName", mockUserEmail, null, 'Y'
        );
        Project mockProject = new Project(
                mockProjectId, "project Title", "project Content", "project Team", "s3Url", "testLink", 'Y'
        );
        ProjectMember mockPM = new ProjectMember(
                1L, 'L', 'Y', mockLeaderUser, mockProject
        );
        ProjectMember mockMember = new ProjectMember(
                2L, 'M', 'Y', mockMemberUser, mockProject
        );
        List<ProjectMember> memberList = new ArrayList<>();
        memberList.add(mockPM);
        memberList.add(mockMember);

        when(projectRepository.findById(mockProjectId)).thenReturn(Optional.of(mockProject));
        when(userRepository.findByEmail(mockUserEmail)).thenReturn(Optional.of(mockMemberUser));
        when(projectMemberRepository.findByProject(mockProject)).thenReturn(memberList);

        // 예외 메시지 검증용
        String expectedExceptionMessage = String.valueOf(NOT_PROJECT_PM);

        // 테스트 실행 및 예외 검증
        assertThrows(CustomException.class, () -> projectService.modifyProject(mockProjectId, mockUserEmail, mockReqDTO), expectedExceptionMessage);

        verify(projectRepository, times(1)).findById(mockProjectId);
        verify(userRepository, times(1)).findByEmail(mockUserEmail);
        verify(projectMemberRepository, times(1)).findByProject(mockProject);
    }

    @Test
    @DisplayName("3.3 프로젝트 삭제")
    void testRemoveProject() {
        // Mock data
        Long mockProjectId = 123L;
        Project mockProject = new Project(
                mockProjectId, "project Title", "project Content", "project Team", null, "testLink", 'Y'
        );

        // projectRepository.findById() 메서드 동작 가짜 구현(Mock)
        when(projectRepository.findById(mockProjectId)).thenReturn(Optional.of(mockProject));

        // 테스트할 메서드 실행
        String result = projectService.removeProject(mockProjectId);

        // 필요한 메서드가 호출되었는지 검증
        verify(projectRepository, times(1)).findById(mockProjectId);
        verify(projectRepository, times(1)).deleteById(mockProject.getProjectId());
        verify(issueRepository, times(1)).deleteByIssueNum();
        verify(releaseApprovalRepository, times(1)).deleteByReleaseApproval();

        // 결과 검증
        assertEquals("프로젝트가 삭제되었습니다.", result);
    }

    @Test
    @DisplayName("3.4 프로젝트 조회")
    void testFindProjects() {
        // Mock 데이터 설정
        String mockEmail = "test@example.com";
        User mockUser1 = new User(
                "Test User1", mockEmail, null, 'Y'
        );
        User mockUser2 = new User(
                "Test User2", "test@releaser.com", null, 'Y'
        );
        List<ProjectMember> projectMemberList = new ArrayList<>();
        Project mockProject1 = new Project(
                1L, "test project1Title", "test project1Content", "test project1Team",
                null, "testLink", 'Y'
        );
        Project mockProject2 = new Project(
                2L, "test project2Title", "test project2Content", "test project2Team",
                null, "testLink", 'Y'
        );

        // userRepository.findByEmail() 메서드 동작 가짜 구현(Mock)
        when(userRepository.findByEmail(mockEmail)).thenReturn(Optional.of(mockUser1));

        // projectMemberRepository.findByUser() 메서드 동작 가짜 구현(Mock)
        when(projectMemberRepository.findByUser(mockUser1)).thenReturn(projectMemberList);

        // project1에 대한 ProjectMember 객체 생성 및 리스트에 추가
        ProjectMember mockProjectMember1 = new ProjectMember(
                1L, 'L', 'Y', mockUser1, mockProject1
        );
        projectMemberList.add(mockProjectMember1);

        // project2에 대한 ProjectMember 객체 생성 및 리스트에 추가
        ProjectMember projectMember2 = new ProjectMember(
                2L, 'M', 'Y', mockUser2, mockProject2
        );
        projectMemberList.add(projectMember2);

        // userRepository.findByEmail() 메서드 동작 가짜 구현(Mock)
        when(userRepository.findByEmail(mockEmail)).thenReturn(Optional.of(mockUser1));

        // projectMemberRepository.findByUser() 메서드 동작 가짜 구현(Mock)
        when(projectMemberRepository.findByUser(mockUser1)).thenReturn(projectMemberList);

        // 테스트할 메서드 실행
        GetProjectResponseDTO result = projectService.findProjects(mockEmail);

        // 예상되는 GetProjectResponseDTO 객체 생성
        List<GetProjectDataDTO> expectedGetCreateProjectList = new ArrayList<>();
        List<GetProjectDataDTO> expectedGetEnterProjectList = new ArrayList<>();

        expectedGetCreateProjectList.add(modelMapper.map(mockProject1, GetProjectDataDTO.class));
        expectedGetEnterProjectList.add(modelMapper.map(mockProject2, GetProjectDataDTO.class));

        GetProjectResponseDTO expectedResponse = GetProjectResponseDTO.builder()
                .getCreateProjectList(expectedGetCreateProjectList)
                .getEnterProjectList(expectedGetEnterProjectList)
                .build();

        // 결과 검증
        assertEquals(expectedResponse.getGetCreateProjectList().size(), result.getGetCreateProjectList().size());
        assertEquals(expectedResponse.getGetEnterProjectList().size(), result.getGetEnterProjectList().size());

        // 필요한 메서드가 호출되었는지 검증
        verify(userRepository, times(1)).findByEmail(mockEmail);
        verify(projectMemberRepository, times(1)).findByUser(mockUser1);
    }

    @Test
    @DisplayName("10.1 프로젝트 내 통합검색 - 이슈 검색")
    void testFindIssueSearch() {
        Long mockProjectId = 1L;
        String mockFilterType = "issue";

        FilterIssueRequestDTO mockIssueReqDTO = new FilterIssueRequestDTO(
                Date.valueOf("2023-08-01"), Date.valueOf("2023-08-09"), 1L,
                "1.0.0", "1.2.0",
                "NEW", "title"
        );
        User mockUser = new User(
                "userName", "test@releaser.com", null, 'Y'

        );
        Project mockProject = new Project(
                mockProjectId, "project Title", "project Content", "project Team", null, "testLink", 'Y'
        );
        ProjectMember mockMember = new ProjectMember(
                1L, 'L', 'Y', mockUser, mockProject
        );

        when(projectRepository.getProjectMemberPostionPM(mockProjectId)).thenReturn(mockMember);

        ProjectSearchResponseDTO result = projectService.findProjectSearch(mockProjectId, mockFilterType, mockIssueReqDTO, null);

        assertNotNull(result);

        verify(projectRepository, times(1)).getProjectMemberPostionPM(mockProjectId);
    }

    @Test
    @DisplayName("10.1 프로젝트 내 통합검색 - 릴리즈 검색")
    void testFindReleaseSearch() {
        Long mockProjectId = 1L;
        String mockFilterType = "release";

        FilterReleaseRequestDTO mockReleaseReqDTO = new FilterReleaseRequestDTO(
                "1.0.0", "2.0.0", "Title"
        );
        User mockUser = new User(
                "userName", "test@releaser.com", null, 'Y'

        );
        Project mockProject = new Project(
                mockProjectId, "project Title", "project Content", "project Team", null, "testLink", 'Y'
        );
        ProjectMember mockMember = new ProjectMember(
                1L, 'L', 'Y', mockUser, mockProject
        );

        when(projectRepository.getProjectMemberPostionPM(mockProjectId)).thenReturn(mockMember);

        ProjectSearchResponseDTO result = projectService.findProjectSearch(mockProjectId, mockFilterType, null, mockReleaseReqDTO);

        assertNotNull(result);

        verify(projectRepository, times(1)).getProjectMemberPostionPM(mockProjectId);
    }


}





