package com.momentum.releaser.domain.user.application;

import com.momentum.releaser.domain.user.dao.UserRepository;
import com.momentum.releaser.domain.user.domain.User;
import com.momentum.releaser.domain.user.dto.UserRequestDto.UserUpdateImgRequestDto;
import com.momentum.releaser.domain.user.dto.UserResponseDto.UserProfileImgResponseDto;
import com.momentum.releaser.domain.user.mapper.UserMapper;
import com.momentum.releaser.global.config.aws.S3Upload;
import com.momentum.releaser.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static com.momentum.releaser.global.common.Base64.getImageUrlFromBase64;
import static com.momentum.releaser.global.common.CommonEnum.DEFAULT_USER_PROFILE_IMG;
import static com.momentum.releaser.global.config.BaseResponseStatus.*;

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
    public UserProfileImgResponseDto findUserProfileImg(String userEmail) {
        User user = getUserByEmail(userEmail);
        return UserMapper.INSTANCE.toUserProfileImgResponseDto(user);
    }

    /**
     * 1.2 사용자 프로필 이미지 변경
     */
    @Transactional
    @Override
    public String modifyUserProfileImg(Long userId, UserUpdateImgRequestDto userUpdateImgRequestDto) throws IOException {
        User user = getUserById(userId);
        deleteIfExistProfileImg(user);
        user.updateImg(uploadUserProfileImg(userUpdateImgRequestDto));
        return "사용자 프로필 이미지 변경에 성공하였습니다.";
    }

    /**
     * 1.3 사용자 프로필 이미지 삭제
     */
    @Override
    public String removeUserProfileImg(Long userId) {
        User user = getUserById(userId);
        deleteIfExistProfileImg(user);
        saveAfterDeleteProfileImg(user);
        return "사용자 프로필 이미지 삭제에 성공하였습니다.";
    }

    // =================================================================================================================

    /**
     * 사용자 식별 번호를 이용해 사용자 엔티티를 가져온다.
     */
    private User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new CustomException(NOT_EXISTS_USER));
    }

    /**
     * 사용자 이메일을 이용해 사용자 엔티티를 가져온다.
     */
    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new CustomException(NOT_EXISTS_USER));
    }

    /**
     * 사용자로부터 받은 프로필 이미지를 S3에 업로드한다.
     */
    private String uploadUserProfileImg(UserUpdateImgRequestDto userUpdateImgRequestDto) throws IOException {
        String img = userUpdateImgRequestDto.getImg();

        if (img.isEmpty()) {
            // 만약 사용자로부터 받은 이미지 데이터가 없는 경우 기본 프로필로 대체한다.
            return DEFAULT_USER_PROFILE_IMG.url();
        }

        // Base64로 인코딩된 이미지 파일을 파일 형태로 가져온다.
        File file = getImageUrlFromBase64(img);

        String url = s3Upload.upload(file, file.getName(), "users");

        if (file.delete()) {
            return url;
        } else {
            throw new CustomException(FAILED_TO_UPDATE_USER_PROFILE_IMG);
        }
    }

    /**
     * 사용자의 이미지 값이 null이 아닌 경우 한 번 지운다.
     */
    private void deleteIfExistProfileImg(User user) {
        // 사용자의 프로필 이미지가 기본 이미지도, null도 아닌 경우 기존에 저장된 파일을 S3에서 삭제한다.
        if (!Objects.equals(user.getImg(), DEFAULT_USER_PROFILE_IMG.url()) && user.getImg() != null) {
            s3Upload.delete(user.getImg().substring(55));
        }
    }

    /**
     * 사용자의 프로필 이미지 파일을 S3에서 삭제한 후 데이터베이스에서 값을 지운다.
     */
    private void saveAfterDeleteProfileImg(User user) {
        user.updateImg(DEFAULT_USER_PROFILE_IMG.url());
        userRepository.save(user);
    }
}
