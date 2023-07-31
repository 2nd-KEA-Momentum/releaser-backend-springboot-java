package com.momentum.releaser.domain.user.application;

import javax.mail.MessagingException;

import com.momentum.releaser.domain.user.dto.UserRequestDto.ConfirmEmailRequestDTO;

public interface EmailService {

    /**
     * 2.6 이메일 인증
     * @return 이메일 인증 코드 메일 전송 성공 메시지
     */
    String confirmEmail(ConfirmEmailRequestDTO confirmEmailRequestDTO) throws MessagingException;
}
