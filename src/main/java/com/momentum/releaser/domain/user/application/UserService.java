package com.momentum.releaser.domain.user.application;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserService {

    /**
     * 1.1 사용자 프로필 이미지 수정
     */
    String updateUserProfileImg(Long userId, MultipartFile multipartFile) throws IOException;
}
