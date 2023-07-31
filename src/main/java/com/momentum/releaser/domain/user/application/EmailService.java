package com.momentum.releaser.domain.user.application;

import javax.mail.MessagingException;

import com.momentum.releaser.domain.user.dto.UserRequestDto.ConfirmEmailRequestDTO;
import com.momentum.releaser.domain.user.dto.UserResponseDto.ConfirmEmailResponseDTO;

public interface EmailService {

    /**
     * 1.5 이메일 인증
     * @return 이메일 인증 코드
     */
    ConfirmEmailResponseDTO confirmEmail(ConfirmEmailRequestDTO confirmEmailRequestDTO) throws MessagingException;
}
