package com.momentum.releaser.domain.user.application;

import static com.momentum.releaser.global.config.BaseResponseStatus.*;

import java.util.Optional;

import com.momentum.releaser.domain.user.dto.AuthResponseDto.UserInfoResponseDTO;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.momentum.releaser.domain.user.dao.AuthPasswordRepository;
import com.momentum.releaser.domain.user.dao.RefreshTokenRepository;
import com.momentum.releaser.domain.user.dao.UserRepository;
import com.momentum.releaser.domain.user.domain.AuthPassword;
import com.momentum.releaser.domain.user.domain.RefreshToken;
import com.momentum.releaser.domain.user.domain.User;
import com.momentum.releaser.domain.user.dto.AuthRequestDto.UserInfoReqestDTO;
import com.momentum.releaser.domain.user.dto.AuthRequestDto.UserLoginReqestDTO;
import com.momentum.releaser.domain.user.dto.TokenDto;
import com.momentum.releaser.global.exception.CustomException;
import com.momentum.releaser.global.jwt.JwtTokenProvider;


/**
 * 사용자 인증과 관련된 기능을 제공하는 서비스 구현 클래스입니다.
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AuthPasswordRepository authPasswordRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final ModelMapper modelMapper;

    /**
     * 2.1 회원가입
     * <p>
     * 사용자 정보를 받아 회원가입을 처리합니다.
     *
     * @param userInfoReq 회원가입 요청 객체
     * @return UserInfoResponseDTO 회원가입 성공 시 사용자 정보를 담은 DTO
     */
    @Override
    @Transactional
    public UserInfoResponseDTO addSignUpUser(UserInfoReqestDTO userInfoReq) {
        // Email 중복 체크
        validateUniqueEmail(userInfoReq.getEmail());
        // 사용자 정보 저장
        User user = createUser(userInfoReq);
        // 패스워드 암호화 후 저장
        createAndSaveAuthPassword(user, userInfoReq.getPassword());
        return modelMapper.map(user, UserInfoResponseDTO.class);
    }

    /*
     * 주어진 이메일이 이미 등록되어 있는지 확인하고, 중복된 이메일일 경우 예외 발생
     *
     * @throws CustomException 이미 등록된 이메일인 경우 발생하는 예외
     */
    private void validateUniqueEmail(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new CustomException(OVERLAP_CHECK_EMAIL);
        }
    }

    // 사용자 정보를 받아서 데이터베이스에 새로운 사용자 생성하고 저장
    private User createUser(UserInfoReqestDTO userInfoReq) {
        return userRepository.save(modelMapper.map(userInfoReq, User.class));
    }

    // 사용자의 비밀번호를 암호화하여 인증 정보를 저장
    private void createAndSaveAuthPassword(User user, String password) {
        String encryptPassword = passwordEncoder.encode(password);
        AuthPassword authPassword = AuthPassword.builder()
                .user(user)
                .password(encryptPassword)
                .build();
        authPasswordRepository.save(authPassword);
    }


    /**
     * 2.2 이메일 로그인
     *
     * 이메일과 비밀번호로 사용자를 로그인합니다.
     *
     * @param userLoginReq 로그인 요청 객체
     * @return TokenDto 로그인 성공 시 토큰 정보를 담은 DTO
     */
    @Override
    @Transactional
    public TokenDto saveLoginUser(UserLoginReqestDTO userLoginReq) {
        // 로그인한 유저 정보 저장
        Authentication authentication = authenticateUser(userLoginReq.getEmail(), userLoginReq.getPassword());
        // Token 생성
        TokenDto tokenDto = jwtTokenProvider.generateToken(authentication);
        // Refresh Token 관리
        manageRefreshToken(userLoginReq.getEmail(), tokenDto.getRefreshToken());
        return tokenDto;
    }

    // 이메일과 비밀번호로 사용자를 인증하고, 인증 객체를 반환
    private Authentication authenticateUser(String email, String password) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(email, password);
        return authenticationManagerBuilder.getObject().authenticate(authentication);
    }

    /*
     * 새로 발급받은 Refresh Token 관리
     * 이미 해당 사용자의 Refresh Token 존재하면 해당 토큰을 업데이트
     * 존재하지 않으면 새로운 토큰을 생성하여 저장
     */
    private void manageRefreshToken(String email, String refreshToken) {
        Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findByUserEmail(email);

        if (optionalRefreshToken.isPresent()) {
            RefreshToken existingRefreshToken = optionalRefreshToken.get();
            existingRefreshToken.updateToken(refreshToken);
            refreshTokenRepository.save(existingRefreshToken);
        } else {
            RefreshToken newToken = new RefreshToken(refreshToken, email);
            refreshTokenRepository.save(newToken);
        }
    }


    /**
     * 2.3 Token 재발급
     *
     * 기존의 Access Token과 Refresh Token을 사용하여 새로운 Access Token을 발급받습니다.
     *
     * @param accessToken  기존의 Access Token
     * @param refreshToken 기존의 Refresh Token
     * @return TokenDto Token 재발급 성공 시 새로운 Access Token 정보를 담은 DTO
     */
    @Override
    @Transactional
    public TokenDto saveRefreshUser(String accessToken, String refreshToken) {
        // Refresh Token 검증 및 사용자 이메일 가져오기
        String email = validateAndGetEmailFromRefreshToken(refreshToken);
        // Access Token에서 유저 정보 가져오기
        Authentication authentication = validateAndGetAuthenticationFromAccessToken(accessToken);
        // 새로운 Access Token 생성
        String newAccessToken = jwtTokenProvider.generateAccessToken(authentication);
        return TokenDto.builder()
                .grantType("Bearer")
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .build();
    }

    /*
     * 주어진 Refresh Token으로 사용자 이메일을 확인하고 반환
     * 만약 Refresh Token이 유효하지 않거나 해당 사용자의 Refresh Token이 존재하지 않을 경우 예외를 발생
     *
     * @throws Refresh Token이 유효하지 않거나 해당 사용자의 Refresh Token이 존재하지 않을 경우 발생하는 예외
     */
    private String validateAndGetEmailFromRefreshToken(String refreshToken) {
        String email = jwtTokenProvider.getEmailFromToken(refreshToken);
        Optional<RefreshToken> existRefreshToken = refreshTokenRepository.findByUserEmail(email);
        if (existRefreshToken.isEmpty() || !jwtTokenProvider.validateToken(refreshToken)) {
            throw new CustomException(INVALID_REFRESH_TOKEN);
        }
        return email;
    }

    // 주어진 Access Token으로 사용자 인증 객체 확인하고 반환
    private Authentication validateAndGetAuthenticationFromAccessToken(String accessToken) {
        return jwtTokenProvider.getAuthentication(accessToken);
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
