package com.momentum.releaser.domain.user.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserRequestDto {

    /**
     * 1.2 사용자 프로필 이미지 변경
     */
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class UserUpdateImgRequestDto {
        private String img;

        @Builder
        public UserUpdateImgRequestDto(String img) {
            this.img = img;
        }
    }

    /**
     * 1.5 이메일 인증
     */
    @Getter
    @NoArgsConstructor
    public static class ConfirmEmailRequestDTO {
        @NotEmpty(message = "이메일을 입력해 주세요.")
        @Email(message = "올바르지 않은 이메일 형식입니다.")
        private String email;

        @Builder
        public ConfirmEmailRequestDTO(String email) {
            this.email = email;
        }
    }
}
