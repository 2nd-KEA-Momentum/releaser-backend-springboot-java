package com.momentum.releaser.domain.user.application;

import javax.mail.MessagingException;

import com.momentum.releaser.domain.user.dto.AuthRequestDto.ConfirmEmailRequestDTO;
import com.momentum.releaser.domain.user.dto.AuthRequestDto.SendEmailRequestDTO;
import com.momentum.releaser.domain.user.dto.AuthResponseDto.ConfirmEmailResponseDTO;

public interface EmailService {

    /**
     * 2.6 이메일 인증
     *
     * @author seonwoo
     * @date 2023-07-31 (월)
     * @return 이메일 인증 코드 메일 전송 성공 메시지
     */
    String sendEmail(SendEmailRequestDTO confirmEmailRequestDTO) throws MessagingException;

    /**
     * 2.7 이메일 인증 확인
     *
     * @author seonwoo
     * @date 2023-08-01 (화)
     * @param userEmail 사용자 이메일
     * @param confirmEmailRequestDTO 사용자 이메일 인증 확인 코드
     * @return ConfirmEmailResponseDTO 사용자 이메일
     */
    ConfirmEmailResponseDTO confirmEmail(String userEmail, ConfirmEmailRequestDTO confirmEmailRequestDTO);
}
