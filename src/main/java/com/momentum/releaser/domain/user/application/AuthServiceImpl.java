package com.momentum.releaser.domain.user.application;

import static com.momentum.releaser.global.config.BaseResponseStatus.*;

import java.util.Optional;

import com.momentum.releaser.domain.user.dto.AuthResponseDto.UserInfoResponseDTO;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
     *
     * @author chaeanna
     * @date 2023-07-18
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

    /**
     * 2.2 이메일 로그인
     *
     * @author chaeanna
     * @date 2023-07-18
     * @param userLoginReq 이메일 로그인 요청 객체
     * @return TokenDto 로그인 성공 시 발급된 토큰 정보를 담은 DTO
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


    /**
     * 2.3 Token 재발급
     *
     * @author chaeanna
     * @date 2023-07-19
     * @param accessToken  기존의 Access Token
     * @param refreshToken 새로 발급받은 Refresh Token
     * @return TokenDto 재발급된 Access Token과 기존의 Refresh Token을 담은 DTO
     * @throws CustomException Refresh Token이 유효하지 않거나 해당 사용자의 Refresh Token이 존재하지 않을 경우 발생하는 예외
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



    /**
     * 2.4 카카오 로그인
     */

    /**
     * 2.5 구글 로그인
     */

    /**
     * 2.6 로그아웃
     */

    // =================================================================================================================

    /**
     * 주어진 이메일이 이미 등록되어 있는지 확인하고, 중복된 이메일일 경우 예외 발생
     *
     * @author chaeanna
     * @date 2023-07-18
     * @param email 확인할 이메일
     * @throws CustomException 이미 등록된 이메일인 경우 발생하는 예외
     */
    private void validateUniqueEmail(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new CustomException(OVERLAP_CHECK_EMAIL);
        }
    }


    /**
     * 사용자 정보를 받아서 새로운 사용자 생성하고 저장
     *
     * @author chaeanna
     * @date 2023-07-18
     * @param userInfoReq 사용자 정보 요청 객체
     * @return 생성된 사용자 엔티티
     */
    private User createUser(UserInfoReqestDTO userInfoReq) {
        return userRepository.save(modelMapper.map(userInfoReq, User.class));
    }


    /**
     * 사용자의 비밀번호를 암호화하여 인증 정보 저장
     *
     * @author chaeanna
     * @date 2023-07-18
     * @param user 사용자 엔티티
     * @param password 사용자 비밀번호
     */
    private void createAndSaveAuthPassword(User user, String password) {
        // 비밀번호를 암호화하여 저장
        String encryptPassword = passwordEncoder.encode(password);

        // 암호화된 비밀번호와 사용자 엔티티로 인증 정보 생성
        AuthPassword authPassword = AuthPassword.builder()
                .user(user)
                .password(encryptPassword)
                .build();

        // 인증 정보를 데이터베이스에 저장
        authPasswordRepository.save(authPassword);
    }


    /**
     * 주어진 이메일과 비밀번호로 사용자 인증하고, 인증 객체 반환
     *
     * @author chaeanna
     * @date 2023-07-18
     * @param email 사용자 이메일
     * @param password 사용자 비밀번호
     * @return 인증 객체
     */
    private Authentication authenticateUser(String email, String password) {
        // 주어진 이메일과 비밀번호로 인증 객체 생성
        Authentication authentication = new UsernamePasswordAuthenticationToken(email, password);

        // 인증 매니저를 통해 인증 처리 후 인증 객체 반환
        return authenticationManagerBuilder.getObject().authenticate(authentication);
    }


    /**
     * 새로 발급받은 Refresh Token 관리
     *
     * 이미 해당 사용자의 Refresh Token이 존재하면 해당 토큰을 업데이트하고 저장합니다.
     * 존재하지 않으면 새로운 토큰을 생성하여 저장합니다.
     *
     * @author chaeanna
     * @date 2023-07-18
     * @param email 사용자 이메일
     * @param refreshToken 새로 발급받은 Refresh Token
     */
    private void manageRefreshToken(String email, String refreshToken) {
        // 해당 사용자의 Refresh Token 찾기
        Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findByUserEmail(email);

        // 이미 해당 사용자의 Refresh Token이 존재하면 업데이트
        if (optionalRefreshToken.isPresent()) {
            RefreshToken existingRefreshToken = optionalRefreshToken.get();
            existingRefreshToken.updateToken(refreshToken);
            refreshTokenRepository.save(existingRefreshToken);
        }
        // 존재하지 않으면 새로운 토큰을 생성하여 저장
        else {
            RefreshToken newToken = new RefreshToken(refreshToken, email);
            refreshTokenRepository.save(newToken);
        }
    }


    /**
     * 주어진 Refresh Token으로 사용자 이메일 확인하고 반환
     * 만약 Refresh Token이 유효하지 않거나 해당 사용자의 Refresh Token이 존재하지 않을 경우 예외를 발생시킵니다.
     *
     * @author chaeanna
     * @date 2023-07-19
     * @param refreshToken 확인할 Refresh Token
     * @return 해당 사용자의 이메일
     * @throws CustomException Refresh Token이 유효하지 않거나 해당 사용자의 Refresh Token이 존재하지 않을 경우 발생하는 예외
     */
    private String validateAndGetEmailFromRefreshToken(String refreshToken) {
        // Refresh Token에서 사용자 이메일 추출
        String email = jwtTokenProvider.getEmailFromToken(refreshToken);

        // 해당 사용자의 Refresh Token 찾기
        Optional<RefreshToken> existRefreshToken = refreshTokenRepository.findByUserEmail(email);

        // Refresh Token이 유효하지 않거나
        // 해당 사용자의 Refresh Token이 존재하지 않으면 예외 발생
        if (existRefreshToken.isEmpty() || !jwtTokenProvider.validateToken(refreshToken)) {
            throw new CustomException(INVALID_REFRESH_TOKEN);
        }

        return email;
    }


    /**
     * 주어진 Access Token으로 사용자 인증 객체 확인하고 반환
     *
     * @author chaeanna
     * @date 2023-07-19
     * @param accessToken 확인할 Access Token
     * @return 인증 객체 (Authentication)
     */
    private Authentication validateAndGetAuthenticationFromAccessToken(String accessToken) {
        // 주어진 Access Token으로 사용자 인증 객체 가져옴
        return jwtTokenProvider.getAuthentication(accessToken);
    }



}
