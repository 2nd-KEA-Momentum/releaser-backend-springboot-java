package com.momentum.releaser.domain.user.api;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

import com.momentum.releaser.domain.user.dto.AuthRequestDto.UserInfoReqestDTO;
import com.momentum.releaser.domain.user.dto.AuthRequestDto.UserLoginReqestDTO;
import com.momentum.releaser.domain.user.dto.AuthResponseDto.UserInfoResponseDTO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.momentum.releaser.domain.user.application.AuthService;
import com.momentum.releaser.domain.user.dto.TokenDto;
import com.momentum.releaser.global.config.BaseResponse;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;



/**
 * AuthController는 사용자 인증과 관련된 API 엔드포인트를 처리하는 컨트롤러입니다.
 * 회원가입, 로그인, Token 재발급 등의 기능을 제공합니다.
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Validated
public class AuthController {

    private final AuthService authService;

    /**
     * 2.1 회원가입
     *
     * 사용자 정보를 받아 회원가입을 처리합니다.
     *
     * @param userInfoReq 회원가입 요청 객체
     * @return UserInfoResponseDTO 회원가입 성공 시 사용자 정보를 담은 DTO
     */
    @PostMapping("/signup")
    public BaseResponse<UserInfoResponseDTO> signUpUserAdd(
            @RequestBody @NotNull(message = "정보를 입력해주세요.") UserInfoReqestDTO userInfoReq) {
        return new BaseResponse<>(authService.addSignUpUser(userInfoReq));
    }

    /**
     * 2.2 이메일 로그인
     *
     * 이메일과 비밀번호로 사용자를 로그인합니다.
     *
     * @param userLoginReq 로그인 요청 객체
     * @return TokenDto 로그인 성공 시 토큰 정보를 담은 DTO
     */
    @PostMapping("/login")
    public BaseResponse<TokenDto> loginUserSave(
            @RequestBody @NotNull(message = "정보를 입력해주세요.") UserLoginReqestDTO userLoginReq) {
        return new BaseResponse<>(authService.saveLoginUser(userLoginReq));
    }

    /**
     * 2.3 Token 재발급
     *
     * 기존의 Access Token과 Refresh Token을 사용하여 새로운 Access Token을 발급받습니다.
     *
     * @param request HTTP 요청 객체
     * @return TokenDto Token 재발급 성공 시 새로운 Access Token 정보를 담은 DTO
     */
    @PostMapping("/refresh")
    public BaseResponse<TokenDto> refreshUserSave(HttpServletRequest request) {
        String accessToken = extractTokenFromAuthorizationHeader(request.getHeader("Access_Token"));
        String refreshToken = extractTokenFromAuthorizationHeader(request.getHeader("Refresh_Token"));
        return new BaseResponse<>(authService.saveRefreshUser(accessToken, refreshToken));
    }

    /**
     * Authorization 헤더에서 토큰을 추출합니다.
     *
     * @param authorizationHeader Authorization 헤더 값
     * @return 추출된 토큰
     */
    private String extractTokenFromAuthorizationHeader(String authorizationHeader) {
        return authorizationHeader.replace("Bearer ", "");
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
