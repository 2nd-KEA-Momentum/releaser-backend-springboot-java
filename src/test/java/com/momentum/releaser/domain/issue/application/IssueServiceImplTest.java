package com.momentum.releaser.domain.issue.application;

import com.momentum.releaser.domain.issue.dao.IssueNumRepository;
import com.momentum.releaser.domain.issue.dao.IssueOpinionRepository;
import com.momentum.releaser.domain.issue.dao.IssueRepository;
import com.momentum.releaser.domain.project.dao.ProjectMemberRepository;
import com.momentum.releaser.domain.project.dao.ProjectRepository;
import com.momentum.releaser.domain.release.dao.release.ReleaseRepository;
import com.momentum.releaser.domain.user.dao.UserRepository;
import org.junit.jupiter.api.BeforeEach;

import static org.mockito.Mockito.mock;

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



}