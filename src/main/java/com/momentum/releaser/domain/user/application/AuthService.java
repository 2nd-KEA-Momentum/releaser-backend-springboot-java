package com.momentum.releaser.domain.user.application;

import com.momentum.releaser.domain.user.dto.AuthRequestDto.UserInfoReqestDTO;
import com.momentum.releaser.domain.user.dto.AuthRequestDto.UserLoginReqestDTO;
import com.momentum.releaser.domain.user.dto.AuthResponseDto.UserInfoResponseDTO;
import com.momentum.releaser.domain.user.dto.TokenDto;


/**
 * 사용자 인증과 관련된 기능을 제공하는 인터페이스입니다.
 */
public interface AuthService {

    /**
     * 2.1 회원가입
     *
     * 사용자 정보를 받아 회원가입을 처리합니다.
     *
     * @param userInfoReq 회원가입 요청 객체
     * @return UserInfoResponseDTO 회원가입 성공 시 사용자 정보를 담은 DTO
     */
    UserInfoResponseDTO addSignUpUser(UserInfoReqestDTO userInfoReq);


    /**
     * 2.2 이메일 로그인
     *
     * 이메일과 비밀번호로 사용자를 로그인합니다.
     *
     * @param userLoginReq 로그인 요청 객체
     * @return TokenDto 로그인 성공 시 토큰 정보를 담은 DTO
     */
    TokenDto saveLoginUser(UserLoginReqestDTO userLoginReq);

    /**
     * 2.3 Token 재발급
     *
     * 기존의 Access Token과 Refresh Token을 사용하여 새로운 Access Token을 발급받습니다.
     *
     * @param accessToken  기존의 Access Token
     * @param refreshToken 기존의 Refresh Token
     * @return TokenDto Token 재발급 성공 시 새로운 Access Token 정보를 담은 DTO
     */
    TokenDto saveRefreshUser(String accessToken, String refreshToken);


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
