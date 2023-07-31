package com.momentum.releaser.domain.user.application;

import java.util.Random;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import com.momentum.releaser.global.common.property.UrlProperty;
import com.momentum.releaser.global.util.RedisUtil;
import com.momentum.releaser.domain.user.dto.UserRequestDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
     * @param confirmEmailRequestDTO 사용자 이메일 정보가 담긴 요청 클래스
     * @return 이메일 인증 코드 메일 전송 성공 메시지
     * @author seonwoo
     * @date 2023-07-31 (월)
     */
    @Override
    public String confirmEmail(UserRequestDto.ConfirmEmailRequestDTO confirmEmailRequestDTO) throws MessagingException {
        // 메일 전송 시 필요한 정보 설정
        MimeMessage emailForm = createEmailForm(confirmEmailRequestDTO.getEmail());

        // 실제 메일 전송
        javaMailSender.send(emailForm);

        // 유효 시간(3분) 동안 {email, authenticationCode} 저장
        redisUtil.setDataExpire(authenticationCode, confirmEmailRequestDTO.getEmail(), 60 * 3L);

        // 인증 코드 반환
        return "이메일 인증 메일이 전송되었습니다.";
    }

    // =================================================================================================================

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
