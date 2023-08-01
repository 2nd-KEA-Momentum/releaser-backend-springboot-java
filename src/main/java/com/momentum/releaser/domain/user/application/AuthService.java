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
     */
    UserInfoResponseDTO addSignUpUser(UserInfoReqestDTO userInfoReq);


    /**
     * 2.2 이메일 로그인
     */
    TokenDto saveLoginUser(UserLoginReqestDTO userLoginReq);

    /**
     * 2.3 Token 재발급
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
