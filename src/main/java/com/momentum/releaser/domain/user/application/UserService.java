package com.momentum.releaser.domain.user.application;

import java.io.IOException;

import com.momentum.releaser.domain.user.dto.UserRequestDto.UserUpdateImgRequestDTO;
import com.momentum.releaser.domain.user.dto.UserResponseDto.UserProfileImgResponseDTO;

public interface UserService {

    /**
     * 1.1 사용자 프로필 이미지 조회
     */
    UserProfileImgResponseDTO findUserProfileImg(String userEmail);

    /**
     * 1.2 사용자 프로필 이미지 변경
     */
    String modifyUserProfileImg(Long userId, UserUpdateImgRequestDTO userUpdateImgRequestDto) throws IOException;

    /**
     * 1.3 사용자 프로필 이미지 삭제
     */
    String removeUserProfileImg(Long userId);
}
