package com.momentum.releaser.domain.user.api;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.momentum.releaser.domain.user.dto.AuthRequestDto;
import com.momentum.releaser.domain.user.dto.AuthRequestDto.ConfirmEmailRequestDTO;
import com.momentum.releaser.domain.user.dto.AuthResponseDto;
import com.momentum.releaser.domain.user.dto.AuthResponseDto.ConfirmEmailResponseDTO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.momentum.releaser.domain.user.dto.AuthRequestDto.SendEmailRequestDTO;
import com.momentum.releaser.domain.user.application.EmailService;
import com.momentum.releaser.domain.user.dto.AuthRequestDto.UserInfoReqestDTO;
import com.momentum.releaser.domain.user.dto.AuthRequestDto.UserLoginReqestDTO;
import com.momentum.releaser.domain.user.dto.AuthResponseDto.UserInfoResponseDTO;

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
    private final EmailService emailService;

    /**
     * 2.1 회원가입
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
     * 2.3 Token 재발급
     *
     * Authorization 헤더에서 토큰을 추출
     *
     * @param authorizationHeader Authorization 헤더 값
     * @return 추출된 토큰
     */
    private String extractTokenFromAuthorizationHeader(String authorizationHeader) {
        return authorizationHeader.replace("Bearer ", "");
    }

    /**
     * 2.6 이메일 인증
     *
     * @param confirmEmailRequestDTO 인증이 필요한 이메일이 담긴 클래스
     * @return 이메일 인증 코드 메일 전송 성공 메시지
     * @throws MessagingException 이메일 전송 및 작성에 문제가 생긴 경우
     */
    @RequestMapping(value = "/emails", method = RequestMethod.POST, params = "!email")
    public BaseResponse<String> userEmailSend(
            @Valid @RequestBody SendEmailRequestDTO confirmEmailRequestDTO) throws MessagingException {

        return new BaseResponse<>(emailService.sendEmail(confirmEmailRequestDTO));
    }

    /**
     * 2.7 이메일 인증 확인
     *
     * @author seonwoo
     * @date 2023-08-01 (화)
     * @param email 사용자 이메일
     * @param confirmEmailRequestDTO 사용자 이메일 인증 확인 코드
     * @return ConfirmEmailRequestDTO 사용자 이메일
     */
    @RequestMapping(value = "/emails", method = RequestMethod.POST, params = "email")
    public BaseResponse<ConfirmEmailResponseDTO> userEmailConfirm(
            @RequestParam(value = "email") @Email(message = "올바르지 않은 이메일 형식입니다.") String email,
            @Valid @RequestBody ConfirmEmailRequestDTO confirmEmailRequestDTO) {

        return new BaseResponse<>(emailService.confirmEmail(email, confirmEmailRequestDTO));
    }
}
