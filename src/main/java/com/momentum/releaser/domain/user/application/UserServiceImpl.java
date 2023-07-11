package com.momentum.releaser.domain.user.application;

import com.momentum.releaser.domain.user.dao.UserRepository;
import com.momentum.releaser.domain.user.domain.User;
import com.momentum.releaser.domain.user.dto.UserResponseDto;
import com.momentum.releaser.domain.user.mapper.UserMapper;
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
     * FIXME: 생성 시 한 번 파일을 지우고, 다시 생성하는 걸로 바꿔야 한다.
     */
    @Transactional
    @Override
    public String updateUserProfileImg(Long userId, MultipartFile multipartFile) throws IOException {
        User user = getUserById(userId);
        user.updateImg(uploadUserProfileImg(multipartFile));
        return "사용자 프로필 이미지 변경에 성공하였습니다.";
    }

    /**
     * 1.3 사용자 프로필 이미지 삭제
     * FIXME: 삭제 시 파일 지우고, 데이터베이스 값도 지워야 한다.
     * TODO: 기본 이미지를 어디 쪽에서 설정할 것인지를 프론트엔드와 상의 후 결정해야 한다.
     */
    @Override
    public String deleteUserProfileImg(Long userId) {
        User user = getUserById(userId);
        s3Upload.delete(user.getImg().substring(55));
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
        return multipartFile == null ? null : s3Upload.upload(multipartFile, "users");
    }

}
