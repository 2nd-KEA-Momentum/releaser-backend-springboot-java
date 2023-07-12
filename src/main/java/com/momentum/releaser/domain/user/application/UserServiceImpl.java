package com.momentum.releaser.domain.user.application;

import com.momentum.releaser.domain.user.dao.UserRepository;
import com.momentum.releaser.domain.user.domain.User;
import com.momentum.releaser.domain.user.dto.UserResponseDto.UserProfileImgResponseDto;
import com.momentum.releaser.domain.user.mapper.UserMapper;
import com.momentum.releaser.global.config.aws.S3Upload;
import com.momentum.releaser.global.error.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

import static com.momentum.releaser.global.common.CommonEnum.DEFAULT_USER_PROFILE_IMG;
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
    public UserProfileImgResponseDto getUserProfileImg(Long userId) {
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
        deleteIfExistProfileImg(user);
        user.updateImg(uploadUserProfileImg(multipartFile));
        return "사용자 프로필 이미지 변경에 성공하였습니다.";
    }

    /**
     * 1.3 사용자 프로필 이미지 삭제
     */
    @Override
    public String deleteUserProfileImg(Long userId) {
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
     * 사용자로부터 받은 프로필 이미지를 S3에 업로드한다.
     */
    private String uploadUserProfileImg(MultipartFile multipartFile) throws IOException {
        return multipartFile.isEmpty() ? DEFAULT_USER_PROFILE_IMG.url() : s3Upload.upload(multipartFile, "users");
    }

    /**
     * 사용자의 이미지 값이 null이 아닌 경우 한 번 지운다.
     */
    private void deleteIfExistProfileImg(User user) {
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
