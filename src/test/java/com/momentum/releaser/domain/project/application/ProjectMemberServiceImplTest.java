package com.momentum.releaser.domain.project.application;


import com.momentum.releaser.domain.project.dao.ProjectMemberRepository;
import com.momentum.releaser.domain.project.dao.ProjectRepository;
import com.momentum.releaser.domain.release.dao.approval.ReleaseApprovalRepository;
import com.momentum.releaser.domain.release.dao.release.ReleaseRepository;
import com.momentum.releaser.domain.user.dao.UserRepository;
import org.junit.jupiter.api.BeforeEach;

import static org.mockito.Mockito.mock;

class ProjectMemberServiceImplTest {

    private ProjectMemberService projectMemberService;
    private ProjectMemberRepository projectMemberRepository;
    private ProjectRepository projectRepository;
    private UserRepository userRepository;
    private ReleaseApprovalRepository releaseApprovalRepository;
    private ReleaseRepository releaseRepository;

    @BeforeEach
    void setUp() {
        projectMemberService = new ProjectMemberServiceImpl(
                projectMemberRepository, projectRepository, userRepository, releaseApprovalRepository, releaseRepository);
        projectMemberRepository = mock(ProjectMemberRepository.class);
        projectRepository = mock(ProjectRepository.class);
        userRepository = mock(UserRepository.class);
        releaseApprovalRepository = mock(ReleaseApprovalRepository.class);
        releaseRepository = mock(ReleaseRepository.class);
    }

}