package com.momentum.releaser.domain.user.application;

import com.momentum.releaser.domain.user.dao.AuthPasswordRepository;
import com.momentum.releaser.domain.user.dao.RefreshTokenRepository;
import com.momentum.releaser.domain.user.dao.UserRepository;
import com.momentum.releaser.domain.user.domain.AuthPassword;
import com.momentum.releaser.domain.user.domain.RefreshToken;
import com.momentum.releaser.domain.user.domain.User;
import com.momentum.releaser.domain.user.dto.AuthReqDto.UserInfoReq;
import com.momentum.releaser.domain.user.dto.AuthReqDto.UserLoginReq;
import com.momentum.releaser.domain.user.dto.AuthResDto.UserInfoRes;
import com.momentum.releaser.domain.user.dto.TokenDto;
import com.momentum.releaser.global.jwt.JwtTokenProvider;
import com.momentum.releaser.global.error.CustomException;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static com.momentum.releaser.global.config.BaseResponseStatus.*;

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
     */
    @Override
    @Transactional
    public UserInfoRes signUpUser(UserInfoReq userInfoReq) {
        // nickname 중복검사
        if(userRepository.findByEmail(userInfoReq.getEmail()).isPresent()){
            throw new CustomException(OVERLAP_CHECK_EMAIL);
        }
        User user = modelMapper.map(userInfoReq, User.class);
        // 회원가입 성공
        userRepository.save(user);

        // 패스워드 암호화
        String encryptPassword = passwordEncoder.encode(userInfoReq.getPassword());
        AuthPassword authPassword = authPasswordRepository.save(AuthPassword.builder()
                .user(user)
                .password(encryptPassword)
                .build());
        //signUp res
        UserInfoRes userInfoRes = modelMapper.map(user, UserInfoRes.class);
        return userInfoRes;
    }

    /**
     * 2.2 이메일 로그인
     */
    @Override
    @Transactional
    public TokenDto loginUser(UserLoginReq userLoginReq) {
        // 1. Login ID/PW 를 기반으로 Authentication 객체 생성
        // 이때 authentication 는 인증 여부를 확인하는 authenticated 값이 false
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userLoginReq.getEmail(), userLoginReq.getPassword());

        // 2. 실제 검증 (사용자 비밀번호 체크)이 이루어지는 부분
        // authenticate 매서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByUsername 메서드가 실행
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        TokenDto tokenDto = jwtTokenProvider.generateToken(authentication);

        // Refresh 토큰 있는지 확인
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByUserEmail(userLoginReq.getEmail());

        // 있다면 새 토큰 발급 후 업데이트
        // 없다면 새로 만들고 디비 저장
        if(refreshToken.isPresent()) {
            refreshTokenRepository.save(refreshToken.get().updateToken(tokenDto.getRefreshToken()));
        }else {
            RefreshToken newToken = new RefreshToken(tokenDto.getRefreshToken(), userLoginReq.getEmail());
            refreshTokenRepository.save(newToken);
        }

        return tokenDto;

    }

    /**
     * 2.3 Token 재발급
     */
    @Override
    @Transactional
    public TokenDto refreshUser(String accessToken, String refreshToken) {
        String email = jwtTokenProvider.getEmailFromToken(refreshToken);

        // Refresh 토큰 있는지 확인
        Optional<RefreshToken> existRefreshToken = refreshTokenRepository.findByUserEmail(email);

        // Refresh Token의 유효성 검사
        if (existRefreshToken.isEmpty() || !jwtTokenProvider.validateToken(refreshToken)) {
            throw new CustomException(INVALID_REFRESH_TOKEN);
        } else {
            Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
            String newAccessToken = jwtTokenProvider.generateAccessToken(authentication);
            return TokenDto.builder()
                    .grantType("Bearer")
                    .accessToken(newAccessToken)
                    .refreshToken(refreshToken)
                    .build();
        }

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
