package com.momentum.releaser.domain.user.application;


import com.momentum.releaser.domain.user.dao.UserRepository;
import com.momentum.releaser.domain.user.domain.User;
import com.momentum.releaser.domain.user.dto.UserResponseDto;
import com.momentum.releaser.domain.user.mapper.UserMapper;
import com.momentum.releaser.global.config.BaseResponseStatus;
import com.momentum.releaser.global.config.aws.S3Upload;
import com.momentum.releaser.global.error.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.momentum.releaser.global.config.BaseResponseStatus.NOT_EXISTS_USER;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final S3Upload s3Upload;

    /**
     * 1.1 사용자 프로필 이미지 조회
     */
    @Override
    public UserResponseDto.UserProfileImgResponseDto getUserProfileImg(Long userId) {
        User user = getUserById(userId);
        return UserMapper.INSTANCE.toUserProfileImgResponseDto(user);
    }

    /**
     * 1.2 사용자 프로필 이미지 변경
     */
    @Transactional
    @Override
    public String updateUserProfileImg(Long userId, MultipartFile multipartFile) throws IOException {
        User user = getUserById(userId);
        user.updateImg(uploadUserProfileImg(multipartFile));
        return "사용자 프로필 이미지 변경에 성공하였습니다.";
    }

    // =================================================================================================================

    /**
     * 사용자 식별 번호를 이용해 사용자 엔티티를 가져온다.
     */
    private User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new CustomException(NOT_EXISTS_USER));
    }

    /**
     * 사용자로부터 받은 프로필 이미지를 S3에 업로드한다.
     */
    private String uploadUserProfileImg(MultipartFile multipartFile) throws IOException {
        return multipartFile == null ? null : s3Upload.upload(multipartFile, "users");
    }

    /**
     * 프로필 이미지를 S3에서 삭제한다.
     */
    private void deleteUserProfileImg(String url) {
    }

}
