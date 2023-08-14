package com.momentum.releaser.domain.user.application;

import com.momentum.releaser.domain.issue.application.IssueServiceImpl;
import com.momentum.releaser.domain.issue.dao.IssueNumRepository;
import com.momentum.releaser.domain.issue.dao.IssueOpinionRepository;
import com.momentum.releaser.domain.issue.dao.IssueRepository;
import com.momentum.releaser.domain.project.dao.ProjectMemberRepository;
import com.momentum.releaser.domain.project.dao.ProjectRepository;
import com.momentum.releaser.domain.release.dao.approval.ReleaseApprovalRepository;
import com.momentum.releaser.domain.release.dao.release.ReleaseRepository;
import com.momentum.releaser.domain.user.dao.UserRepository;
import com.momentum.releaser.domain.user.domain.User;
import com.momentum.releaser.domain.user.dto.UserRequestDto;
import com.momentum.releaser.domain.user.dto.UserRequestDto.UserUpdateImgRequestDTO;
import com.momentum.releaser.domain.user.dto.UserResponseDto;
import com.momentum.releaser.domain.user.dto.UserResponseDto.UserProfileImgResponseDTO;
import com.momentum.releaser.global.config.aws.S3Upload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {
    
    private UserServiceImpl userService;
    private UserRepository userRepository;
    private ProjectMemberRepository projectMemberRepository;
    private ReleaseApprovalRepository releaseApprovalRepository;
    private S3Upload s3Upload;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        projectMemberRepository = mock(ProjectMemberRepository.class);
        releaseApprovalRepository = mock(ReleaseApprovalRepository.class);
        s3Upload = mock(S3Upload.class);
        userService = new UserServiceImpl(
                userRepository, projectMemberRepository, releaseApprovalRepository, s3Upload
        );
    }

    @Test
    @DisplayName("1.2 사용자 프로필 이미지 변경")
    void testModifyUserProfileImg() throws IOException {
        String mockUserEmail = "test@releaser.com";

        UserUpdateImgRequestDTO mockReqDTO = new UserUpdateImgRequestDTO(
                "data:image/jpeg;base64,imgURL.jpeg"
        );
        User mockUser = new User(
                "testUser1Name", mockUserEmail, "data:image/jpeg;base64,img.jpeg", 'Y'
        );

        when(userRepository.findByEmail(mockUserEmail)).thenReturn(Optional.of(mockUser));

        UserProfileImgResponseDTO result = userService.modifyUserProfileImg(mockUserEmail, mockReqDTO);

        assertNotNull(result);

        verify(userRepository, times(1)).findByEmail(mockUserEmail);
    }


}