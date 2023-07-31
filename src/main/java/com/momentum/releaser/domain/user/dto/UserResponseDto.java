package com.momentum.releaser.domain.user.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserResponseDto {

    /**
     * 1.1 사용자 프로필 이미지 조회
     */
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class UserProfileImgResponseDTO {
        private Long userId;
        private String name;
        private String img;

        @Builder
        public UserProfileImgResponseDTO(Long userId, String name, String img) {
            this.userId = userId;
            this.name = name;
            this.img = img;
        }
    }

    /**
     * 2.6 이메일 인증 (테스트용)
     */
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ConfirmEmailResponseDTO {
        private String code;

        @Builder
        public ConfirmEmailResponseDTO(String code) {
            this.code = code;
        }
    }
}
