package com.momentum.releaser.domain.user.api;

import com.momentum.releaser.domain.user.application.AuthService;
import com.momentum.releaser.domain.user.application.UserServiceImpl;
import com.momentum.releaser.domain.user.dto.AuthReqDto;
import com.momentum.releaser.domain.user.dto.AuthReqDto.UserInfoReq;
import com.momentum.releaser.domain.user.dto.AuthReqDto.UserLoginReq;
import com.momentum.releaser.domain.user.dto.AuthResDto;
import com.momentum.releaser.domain.user.dto.AuthResDto.UserInfoRes;
import com.momentum.releaser.domain.user.dto.TokenDto;
import com.momentum.releaser.global.config.BaseResponse;
import com.momentum.releaser.global.jwt.UserPrincipal;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Validated
public class AuthController {

    private final AuthService authService;

    /**
     * 2.1 이메일 회원가입
     */
    @PostMapping("/signup")
    public BaseResponse<UserInfoRes> signUpUser(
            @RequestBody @NotNull(message = "정보를 입력해주세요.") UserInfoReq userInfoReq) {
        return new BaseResponse<>(authService.signUpUser(userInfoReq));

    }

    /**
     * 2.2 이메일 로그인
     */
    @PostMapping("/login")
    public BaseResponse<TokenDto> loginUser(
            @RequestBody @NotNull(message = "정보를 입력해주세요.") UserLoginReq userLoginReq) {
        return new BaseResponse<>(authService.loginUser(userLoginReq));

    }

    /**
     * 2.3 Token 재발급
     */
    @PatchMapping("/refresh")
    public BaseResponse<TokenDto> refreshUser(HttpServletRequest request) {

        String accessToken = request.getHeader("Access_Token");
        // Authorization 헤더에 전달된 토큰에서 "Bearer " 접두사를 제거하여 실제 토큰 값만 추출
        accessToken = accessToken.replace("Bearer ", "");

        String refreshToken = request.getHeader("Refresh_Token");
        // Authorization 헤더에 전달된 토큰에서 "Bearer " 접두사를 제거하여 실제 토큰 값만 추출
        refreshToken = refreshToken.replace("Bearer ", "");



        return new BaseResponse<>(authService.refreshUser(accessToken, refreshToken));
    }



    /**
     * 2.4 카카오 로그인
     */

    /**
     * 2.5 구글 로그인
     */

    /**
     * 2.6 로그아웃
     */



}
