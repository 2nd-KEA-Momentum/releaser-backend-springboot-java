package com.momentum.releaser.domain.user.application;

import com.momentum.releaser.domain.user.dto.UserRequestDto.UserUpdateImgRequestDto;
import com.momentum.releaser.domain.user.dto.UserResponseDto.UserProfileImgResponseDto;

import java.io.IOException;

public interface UserService {

    /**
     * 1.1 사용자 프로필 이미지 조회
     */
    UserProfileImgResponseDto getUserProfileImg(String userEmail);

    /**
     * 1.2 사용자 프로필 이미지 변경
     */
    String updateUserProfileImg(Long userId, UserUpdateImgRequestDto userUpdateImgRequestDto) throws IOException;

    /**
     * 1.3 사용자 프로필 이미지 삭제
     */
    String deleteUserProfileImg(Long userId);
}
