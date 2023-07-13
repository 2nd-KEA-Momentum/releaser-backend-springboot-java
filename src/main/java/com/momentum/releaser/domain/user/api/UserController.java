package com.momentum.releaser.domain.user.api;

import com.momentum.releaser.domain.user.application.UserServiceImpl;
import com.momentum.releaser.domain.user.dto.UserResponseDto.UserProfileImgResponseDto;
import com.momentum.releaser.global.config.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Min;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Validated
public class UserController {

    private final UserServiceImpl userService;

    /**
     * 1.1 사용자 프로필 이미지 조회
     */
    @GetMapping(value = "/{userId}/images")
    public BaseResponse<UserProfileImgResponseDto> getUserProfileImg(
            @PathVariable @Min(value = 1, message = "사용자 식별 번호는 1 이상의 숫자여야 합니다.") Long userId) {

        return new BaseResponse<>(userService.getUserProfileImg(userId));
    }

    /**
     * 1.2 사용자 프로필 이미지 변경
     */
    @PatchMapping(value = "/{userId}/images")
    public BaseResponse<String> updateUserProfileImg(
            @PathVariable @Min(value = 1, message = "사용자 식별 번호는 1 이상의 숫자여야 합니다.") Long userId,
            @RequestPart("img") MultipartFile multipartFile) throws IOException {

        return new BaseResponse<>(userService.updateUserProfileImg(userId, multipartFile));
    }

    /**
     * 1.3 사용자 프로필 이미지 삭제
     */
    @PostMapping(value = "/{userId}/images")
    public BaseResponse<String> deleteUserProfileImg(
            @PathVariable @Min(value = 1, message = "사용자 식별 번호는 1 이상의 숫자여야 합니다.") Long userId) {

        return new BaseResponse<>(userService.deleteUserProfileImg(userId));
    }
}
