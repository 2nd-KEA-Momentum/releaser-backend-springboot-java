package com.momentum.releaser.domain.user.application;

import com.momentum.releaser.domain.issue.application.IssueServiceImpl;
import com.momentum.releaser.domain.issue.dao.IssueNumRepository;
import com.momentum.releaser.domain.issue.dao.IssueOpinionRepository;
import com.momentum.releaser.domain.issue.dao.IssueRepository;
import com.momentum.releaser.domain.project.dao.ProjectMemberRepository;
import com.momentum.releaser.domain.project.dao.ProjectRepository;
import com.momentum.releaser.domain.release.dao.release.ReleaseRepository;
import com.momentum.releaser.domain.user.dao.AuthPasswordRepository;
import com.momentum.releaser.domain.user.dao.RefreshTokenRepository;
import com.momentum.releaser.domain.user.dao.UserRepository;
import com.momentum.releaser.domain.user.domain.AuthPassword;
import com.momentum.releaser.domain.user.domain.RefreshToken;
import com.momentum.releaser.domain.user.domain.User;
import com.momentum.releaser.domain.user.dto.AuthRequestDto;
import com.momentum.releaser.domain.user.dto.AuthRequestDto.UserInfoReqestDTO;
import com.momentum.releaser.domain.user.dto.AuthRequestDto.UserLoginReqestDTO;
import com.momentum.releaser.domain.user.dto.AuthResponseDto;
import com.momentum.releaser.domain.user.dto.AuthResponseDto.UserInfoResponseDTO;
import com.momentum.releaser.domain.user.dto.TokenDto;
import com.momentum.releaser.global.jwt.JwtTokenProvider;
import com.momentum.releaser.redis.RedisUtil;
import com.momentum.releaser.redis.password.PasswordRedisRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceImplTest {
    
    private AuthServiceImpl authService;
    private PasswordEncoder passwordEncoder;
    private UserRepository userRepository;
    private AuthPasswordRepository authPasswordRepository;
    private RefreshTokenRepository refreshTokenRepository;
    private AuthenticationManagerBuilder authenticationManagerBuilder;
    private JwtTokenProvider jwtTokenProvider;
    private ModelMapper modelMapper;

    private RedisUtil redisUtil;
    private PasswordRedisRepository passwordRedisRepository;

    @BeforeEach
    void setUp() {
        passwordEncoder = mock(PasswordEncoder.class);
        userRepository = mock(UserRepository.class);
        authPasswordRepository = mock(AuthPasswordRepository.class);
        refreshTokenRepository = mock(RefreshTokenRepository.class);
        authenticationManagerBuilder = mock(AuthenticationManagerBuilder.class);
        jwtTokenProvider = mock(JwtTokenProvider.class);
        modelMapper = mock(ModelMapper.class);
        redisUtil = mock(RedisUtil.class);
        passwordRedisRepository = mock(PasswordRedisRepository.class);
        authService = new AuthServiceImpl(passwordEncoder, userRepository, authPasswordRepository, refreshTokenRepository,
                authenticationManagerBuilder, jwtTokenProvider, modelMapper, redisUtil, passwordRedisRepository);
    }

    @Test
    @DisplayName("2.1 회원가입")
    void testAddSignUpUser() {
        UserInfoReqestDTO mockReqDTO = new UserInfoReqestDTO(
                "testUser", "testUser@releaser.com", "password"
        );
        User mockUser = new User(
                "testUser", "testUser@releaser.com", null, 'Y'
        );
        AuthPassword mockPassword = new AuthPassword(
                mockUser, "encryptedPassword", 'Y'
        );
        UserInfoResponseDTO userResDTO = new UserInfoResponseDTO(
                1L, "testUser", "testUser@releaser.com"
        );

        when(userRepository.findByEmail(mockReqDTO.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(modelMapper.map(mockReqDTO, User.class))).thenReturn(mockUser);
        when(passwordEncoder.encode(mockReqDTO.getPassword())).thenReturn("encryptedPassword");
        when(authPasswordRepository.save(any())).thenReturn(mockPassword);
        when(modelMapper.map(mockUser, UserInfoResponseDTO.class)).thenReturn(userResDTO);

        UserInfoResponseDTO result = authService.addSignUpUser(mockReqDTO);

        assertEquals(userResDTO.getName(), result.getName());
        assertEquals(userResDTO.getEmail(), result.getEmail());

        verify(userRepository, times(1)).findByEmail(mockReqDTO.getEmail());
        verify(userRepository, times(1)).save(modelMapper.map(mockReqDTO, User.class));
        verify(passwordEncoder, times(1)).encode(mockReqDTO.getPassword());
        verify(authPasswordRepository, times(1)).save(any(AuthPassword.class));
        verify(modelMapper, times(1)).map(mockUser, UserInfoResponseDTO.class);
    }

    @Test
    @DisplayName("2.2 이메일 로그인")
    void testSaveLoginUser() {
        UserLoginReqestDTO mockReqDTO = new UserLoginReqestDTO(
                "testUser@releaser.com", "password"
        );
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                mockReqDTO.getEmail(), mockReqDTO.getPassword()
        );
        TokenDto mockTokenDTO = new TokenDto(
                "Bearer" , "accessToken", "refreshToken"
        );

        when(authenticationManagerBuilder.getObject()).thenReturn(any(AuthenticationManager.class));
        when(authenticationManagerBuilder.getObject().authenticate(any(Authentication.class))).thenReturn(authentication);
        when(jwtTokenProvider.generateToken(any(Authentication.class))).thenReturn(mockTokenDTO);
        when(refreshTokenRepository.findByUserEmail(mockReqDTO.getEmail())).thenReturn(Optional.empty());

        TokenDto result = authService.saveLoginUser(mockReqDTO);

        assertEquals(mockTokenDTO.getAccessToken(), result.getAccessToken());
        assertEquals(mockTokenDTO.getRefreshToken(), result.getRefreshToken());

        verify(authenticationManagerBuilder.getObject()).authenticate(any(Authentication.class));
        verify(jwtTokenProvider).generateToken(any(Authentication.class));
        verify(refreshTokenRepository).findByUserEmail(mockReqDTO.getEmail());
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

}