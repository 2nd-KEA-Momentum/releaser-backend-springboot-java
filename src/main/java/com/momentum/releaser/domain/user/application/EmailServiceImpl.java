package com.momentum.releaser.domain.user.application;

import java.util.Random;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import com.momentum.releaser.domain.user.dto.AuthRequestDto;
import com.momentum.releaser.domain.user.dto.AuthRequestDto.ConfirmEmailRequestDTO;
import com.momentum.releaser.domain.user.dto.AuthRequestDto.SendEmailRequestDTO;
import com.momentum.releaser.domain.user.dto.AuthResponseDto;
import com.momentum.releaser.domain.user.dto.AuthResponseDto.ConfirmEmailResponseDTO;
import com.momentum.releaser.domain.user.mapper.UserMapper;
import com.momentum.releaser.global.exception.CustomException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import com.momentum.releaser.global.common.property.UrlProperty;
import com.momentum.releaser.global.util.RedisUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.momentum.releaser.global.config.BaseResponseStatus.INVALID_EMAIL_AND_AUTH_CODE;
import static com.momentum.releaser.global.config.BaseResponseStatus.NOT_EXISTS_EMAIL_AND_AUTH_CODE;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    // 이메일 인증 시 필요한 인증 코드
    private String authenticationCode;

    @Value("${spring.mail.username}")
    private String userName;

    private final UrlProperty urlProperty;

    private final JavaMailSender javaMailSender;

    private final RedisUtil redisUtil;

    private final SpringTemplateEngine springTemplateEngine;

    /**
     * 2.6 이메일 인증
     *
     * @author seonwoo
     * @date 2023-07-31 (월)
     */
    @Override
    public String sendEmail(SendEmailRequestDTO confirmEmailRequestDTO) throws MessagingException {
        // Redis에 값이 존재하는지 확인한다.
        deleteIfExistsEmailInRedis(confirmEmailRequestDTO.getEmail());

        // 메일 전송 시 필요한 정보 설정
        MimeMessage emailForm = createEmailForm(confirmEmailRequestDTO.getEmail());

        // 실제 메일 전송
        javaMailSender.send(emailForm);

        // 유효 시간(3분) 동안 {email, authenticationCode} 저장
        redisUtil.setDataExpire(confirmEmailRequestDTO.getEmail(), authenticationCode, 60 * 3L);

        // 인증 코드 반환
        return "이메일 인증 메일이 전송되었습니다.";
    }

    /**
     * 2.7 이메일 인증 확인
     *
     * @param userEmail 사용자 이메일
     * @param confirmEmailRequestDTO 사용자 이메일 인증 확인 코드
     * @return ConfirmEmailResponseDTO 사용자 이메일
     */
    @Override
    public ConfirmEmailResponseDTO confirmEmail(String userEmail, ConfirmEmailRequestDTO confirmEmailRequestDTO) {
        // Redis에 저장된 값과 일치하는지 확인한다.
        int successStatus = verifyEmailAndAuthCode(userEmail, confirmEmailRequestDTO.getAuthCode());

        if (successStatus != 1) {
            // 만약 값이 일치하지 않는다면 예외를 발생시킨다.
            throw new CustomException(INVALID_EMAIL_AND_AUTH_CODE);
        }

        // 만약 일치한다면 이메일 값을 담아 반환한다.
        return ConfirmEmailResponseDTO.builder().email(userEmail).build();
    }

    // =================================================================================================================

    /**
     * 만약 Redis에 해당 이메일로 된 값이 존재한다면 삭제한다.
     *
     * @param email 사용자가 회원가입 하고자 하는 이메일
     */
    private void deleteIfExistsEmailInRedis(String email) {
        if (redisUtil.existsData(email)) {
            redisUtil.deleteData(email);
        }
    }

    /**
     * Redis에 저장된 이메일과 인증 코드 값이 올바른지 확인한다.
     *
     * @author seonwoo
     * @date 2023-08-01 (화)
     * @param email 사용자 이메일
     * @param authCode 사용자 이메일 인증 코드
     * @return 이메일 인증 성공 여부
     */
    private int verifyEmailAndAuthCode(String email, String authCode) {
        // 사용자 이메일 키 값을 가지고 인증 코드를 가져온다.
        String savedAuthCode = redisUtil.getData(email);

        if (savedAuthCode == null) {
            // 만약 유효 시간이 만료되었다면 예외를 발생시킨다.
            throw new CustomException(NOT_EXISTS_EMAIL_AND_AUTH_CODE);
        }

        // 인증 코드가 동일한지 비교한다.
        return savedAuthCode.equals(authCode) ? 1 : 0;
    }

    /**
     * 이메일 인증을 위한 랜덤 인증 코드 생성
     */
    private void createAuthenticationCode() {
        Random random = new Random();
        StringBuilder key = new StringBuilder();

        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(3);

            switch (index) {
                case 0:
                    key.append((char) (random.nextInt(26) + 97));
                    break;
                case 1:
                    key.append((char) (random.nextInt(26) + 65));
                    break;
                case 2:
                    key.append(random.nextInt(9));
                    break;
            }
        }

        authenticationCode = key.toString();
    }

    /**
     * Thymeleaf 템플릿 엔진에 필요한 값을 주입
     *
     * @param code 이메일 인증 코드
     * @param urlBannerProject 프로젝트 배너 이미지 URL
     * @param urlLogoTeam 팀 로고 이미지 URL
     * @return Thymeleaf 템플릿 엔진을 사용하여 mail.html을 렌더링한 결과
     */
    private String setContext(String code, String urlBannerProject, String urlLogoTeam) {
        Context context = new Context();
        context.setVariable("code", code);
        context.setVariable("releaser", urlBannerProject);
        context.setVariable("momentum", urlLogoTeam);
        return springTemplateEngine.process("mail", context); // mail.html
    }

    /**
     * 이메일 양식 작성
     *
     * @param email 이메일 인증 코드 메일을 받는 이메일
     * @return 이메일 양식
     * @throws MessagingException 이메일 전송 및 작성에 문제가 생긴 경우
     */
    private MimeMessage createEmailForm(String email) throws MessagingException {
        // 인증 코드 생성
        createAuthenticationCode();

        // 이메일 내용
        String title = "[Releaser] 이메일 인증 메일입니다.";
        String urlBannerProject = urlProperty.getImage().getBannerProject();
        String urlLogoTeam = urlProperty.getImage().getLogoTeam();

        // 이메일 양식 작성을 위한 정보 설정
        MimeMessage message = javaMailSender.createMimeMessage();
        message.addRecipients(MimeMessage.RecipientType.TO, email); // 보내는 이메일 설정
        message.setSubject(title); // 이메일 제목 설정
        message.setFrom(userName); // 보내는 이메일 설정
        message.setText(setContext(authenticationCode, urlBannerProject, urlLogoTeam), "utf-8", "html");

        return message;
    }
}
