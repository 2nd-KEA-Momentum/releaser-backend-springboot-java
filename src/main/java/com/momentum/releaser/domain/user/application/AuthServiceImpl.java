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
        //Email check
        validateUniqueEmail(userInfoReq.getEmail());
        //userInfo 저장
        User user = createUser(userInfoReq);
        //password 암호화 후 저장
        createAndSaveAuthPassword(user, userInfoReq.getPassword());
        return modelMapper.map(user, UserInfoRes.class);
    }

    private void validateUniqueEmail(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new CustomException(OVERLAP_CHECK_EMAIL);
        }
    }

    private User createUser(UserInfoReq userInfoReq) {
        return userRepository.save(modelMapper.map(userInfoReq, User.class));
    }

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
     */
    @Override
    @Transactional
    public TokenDto loginUser(UserLoginReq userLoginReq) {
        //로그인한 유저 정보 저장
        Authentication authentication = authenticateUser(userLoginReq.getEmail(), userLoginReq.getPassword());
        // Token 생성
        TokenDto tokenDto = jwtTokenProvider.generateToken(authentication);
        // refresh Token check
        manageRefreshToken(userLoginReq.getEmail(), tokenDto.getRefreshToken());
        return tokenDto;
    }

    private Authentication authenticateUser(String email, String password) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(email, password);
        return authenticationManagerBuilder.getObject().authenticate(authentication);
    }

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
     */
    @Override
    @Transactional
    public TokenDto refreshUser(String accessToken, String refreshToken) {
        //Refresh Token check
        String email = validateAndGetEmailFromRefreshToken(refreshToken);
        //Access Toekn에서 유저 정보 가져오기
        Authentication authentication = validateAndGetAuthenticationFromAccessToken(accessToken);
        //새로운 Access Token 생성
        String newAccessToken = jwtTokenProvider.generateAccessToken(authentication);
        return TokenDto.builder()
                .grantType("Bearer")
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private String validateAndGetEmailFromRefreshToken(String refreshToken) {
        String email = jwtTokenProvider.getEmailFromToken(refreshToken);
        Optional<RefreshToken> existRefreshToken = refreshTokenRepository.findByUserEmail(email);
        if (existRefreshToken.isEmpty() || !jwtTokenProvider.validateToken(refreshToken)) {
            throw new CustomException(INVALID_REFRESH_TOKEN);
        }
        return email;
    }

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
