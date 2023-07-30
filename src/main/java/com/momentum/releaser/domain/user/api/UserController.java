package com.momentum.releaser.domain.user.api;

import java.io.IOException;

import javax.validation.constraints.Min;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.momentum.releaser.domain.user.application.UserService;
import com.momentum.releaser.domain.user.dto.UserRequestDto.UserUpdateImgRequestDto;
import com.momentum.releaser.domain.user.dto.UserResponseDto.UserProfileImgResponseDto;
import com.momentum.releaser.global.config.BaseResponse;
import com.momentum.releaser.global.jwt.UserPrincipal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Validated
public class UserController {

    private final UserService userService;

    /**
     * 1.1 사용자 프로필 이미지 조회
     */
    @GetMapping(value = "/images")
    public BaseResponse<UserProfileImgResponseDto> getUserProfileImg(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        return new BaseResponse<>(userService.getUserProfileImg(userPrincipal.getEmail()));
    }

    /**
     * 1.2 사용자 프로필 이미지 변경
     */
    @PatchMapping(value = "/{userId}/images")
    public BaseResponse<String> updateUserProfileImg(
            @PathVariable @Min(value = 1, message = "사용자 식별 번호는 1 이상의 숫자여야 합니다.") Long userId,
            @RequestBody UserUpdateImgRequestDto userUpdateImgRequestDto) throws IOException {

        return new BaseResponse<>(userService.updateUserProfileImg(userId, userUpdateImgRequestDto));
    }

    /**
     * 1.3 사용자 프로필 이미지 삭제
     */
    @PostMapping(value = "/{userId}/images")
    public BaseResponse<String> deleteUserProfileImg(
            @PathVariable @Min(value = 1, message = "사용자 식별 번호는 1 이상의 숫자여야 합니다.") Long userId) {

        return new BaseResponse<>(userService.deleteUserProfileImg(userId));
    }

    //    @Secured(value = UserRoleEnum.Authority.ADMIN)
    @GetMapping("/test")
    public String test(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        String userEmail = userPrincipal.getEmail();
        return "email : " + userEmail;
    }
}
