package com.momentum.releaser.domain.user.api;

import java.io.IOException;

import javax.validation.constraints.Min;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;

import com.momentum.releaser.domain.user.application.UserService;
import com.momentum.releaser.domain.user.dto.UserRequestDto.UserUpdateImgRequestDTO;
import com.momentum.releaser.domain.user.dto.UserResponseDto.UserProfileImgResponseDTO;
import com.momentum.releaser.global.config.BaseResponse;
import com.momentum.releaser.global.jwt.UserPrincipal;

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
    public BaseResponse<UserProfileImgResponseDTO> userProfileImgDetails(@AuthenticationPrincipal UserPrincipal userPrincipal) {

        return new BaseResponse<>(userService.findUserProfileImg(userPrincipal.getEmail()));
    }

    /**
     * 1.2 사용자 프로필 이미지 변경
     */
    @PatchMapping(value = "/{userId}/images")
    public BaseResponse<String> userProfileImgModify(@PathVariable @Min(value = 1, message = "사용자 식별 번호는 1 이상의 숫자여야 합니다.") Long userId,
            @RequestBody UserUpdateImgRequestDTO userUpdateImgRequestDto) throws IOException {

        return new BaseResponse<>(userService.modifyUserProfileImg(userId, userUpdateImgRequestDto));
    }

    /**
     * 1.3 사용자 프로필 이미지 삭제
     */
    @PostMapping(value = "/{userId}/images")
    public BaseResponse<String> userProfileImgRemove(@PathVariable @Min(value = 1, message = "사용자 식별 번호는 1 이상의 숫자여야 합니다.") Long userId) {

        return new BaseResponse<>(userService.removeUserProfileImg(userId));
    }

}
