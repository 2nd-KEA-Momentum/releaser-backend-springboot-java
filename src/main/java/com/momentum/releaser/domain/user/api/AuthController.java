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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
     * 2.3 카카오 로그인
     */

    /**
     * 2.4 구글 로그인
     */

    /**
     * 2.5 로그아웃
     */


}
