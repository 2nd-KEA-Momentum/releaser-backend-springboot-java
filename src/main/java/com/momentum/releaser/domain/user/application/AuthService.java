package com.momentum.releaser.domain.user.application;

import com.momentum.releaser.domain.user.dto.AuthReqDto;
import com.momentum.releaser.domain.user.dto.AuthReqDto.UserLoginReq;
import com.momentum.releaser.domain.user.dto.AuthResDto;
import com.momentum.releaser.domain.user.dto.AuthResDto.UserInfoRes;
import com.momentum.releaser.domain.user.dto.TokenDto;

import javax.servlet.http.HttpServletResponse;

public interface AuthService {
    /**
     * 2.1 회원가입
     */
    UserInfoRes signUpUser(AuthReqDto.UserInfoReq userInfoReq);


    /**
     * 2.2 이메일 로그인
     */
    TokenDto loginUser(UserLoginReq userLoginReq);

    /**
     * 2.3 token 재발급
     */
    TokenDto refreshUser(String accessToken, String refreshToken);


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
