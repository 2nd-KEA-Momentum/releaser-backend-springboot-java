package com.momentum.releaser.domain.user.application;

import com.momentum.releaser.domain.user.dto.UserResponseDto.UserProfileImgResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserService {

    /**
     * 1.1 사용자 프로필 이미지 조회
     */
    UserProfileImgResponseDto getUserProfileImg(Long userId);

    /**
     * 1.2 사용자 프로필 이미지 변경
     */
    String updateUserProfileImg(Long userId, MultipartFile multipartFile) throws IOException;
}
