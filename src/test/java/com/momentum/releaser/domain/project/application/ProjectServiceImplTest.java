package com.momentum.releaser.domain.project.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.momentum.releaser.domain.release.dao.release.ReleaseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import com.momentum.releaser.domain.issue.dao.IssueRepository;
import com.momentum.releaser.domain.project.dao.ProjectMemberRepository;
import com.momentum.releaser.domain.project.dao.ProjectRepository;
import com.momentum.releaser.domain.project.domain.Project;
import com.momentum.releaser.domain.project.domain.ProjectMember;
import com.momentum.releaser.domain.project.dto.ProjectDataDto.GetProjectDataDTO;
import com.momentum.releaser.domain.project.dto.ProjectResponseDto.GetProjectResponseDTO;
import com.momentum.releaser.domain.release.dao.approval.ReleaseApprovalRepository;
import com.momentum.releaser.domain.user.dao.UserRepository;
import com.momentum.releaser.domain.user.domain.User;
import com.momentum.releaser.global.config.aws.S3Upload;

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
        releaseApprovalRepository = mock(ReleaseApprovalRepository.class);
        projectMemberRepository = mock(ProjectMemberRepository.class);
        userRepository = mock(UserRepository.class);
        modelMapper = new ModelMapper(); // modelMapper 초기화
        s3Upload = mock(S3Upload.class);
        projectService = new ProjectServiceImpl(projectRepository, projectMemberRepository, userRepository, issueRepository, releaseRepository, releaseApprovalRepository, modelMapper, s3Upload);
    }

    @Test
    @DisplayName("3.3 프로젝트 삭제")
    void testRemoveProject() {
        // Mock data
        Long mockProjectId = 123L;
        Project mockProject = new Project(
                "project Title",
                "project Content",
                "project Team",
                "",
                "linkkkk",
                'Y'
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
                "Test User1",
                mockEmail,
                "",
                'Y'
        );
        User mockUser2 = new User(
                "Test User2",
                "test@releaser.com",
                "",
                'Y'
        );
        List<ProjectMember> projectMemberList = new ArrayList<>();
        Project mockProject1 = new Project(
                "test project1Title",
                "test project1Content",
                "test project1Team",
                "",
                "lrngwoignw",
                'Y'
        );
        Project mockProject2 = new Project(
                "test project2Title",
                "test project2Content",
                "test project2Team",
                "",
                "sdkfnsfn",
                'Y'
        );

        // userRepository.findByEmail() 메서드 동작 가짜 구현(Mock)
        when(userRepository.findByEmail(mockEmail)).thenReturn(Optional.of(mockUser1));

        // projectMemberRepository.findByUser() 메서드 동작 가짜 구현(Mock)
        when(projectMemberRepository.findByUser(mockUser1)).thenReturn(projectMemberList);

        // project1에 대한 ProjectMember 객체 생성 및 리스트에 추가
        ProjectMember mockProjectMember1 = new ProjectMember(
                'L',
                'Y',
                mockUser1,
                mockProject1
        );
        projectMemberList.add(mockProjectMember1);

        // project2에 대한 ProjectMember 객체 생성 및 리스트에 추가
        ProjectMember projectMember2 = new ProjectMember(
                'M',
                'Y',
                mockUser2,
                mockProject2
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

}





