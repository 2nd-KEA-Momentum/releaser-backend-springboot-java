package com.momentum.releaser.domain.user.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class AuthResponseDto {

    /**
     * 2.1 회원가입
     *
     * 회원가입 성공 시 사용자 정보를 담은 DTO
     */
    @Data
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class UserInfoResponseDTO {
        private Long userId;
        private String name;
        private String email;

        @Builder
        public UserInfoResponseDTO(Long userId, String name, String email) {
            this.userId = userId;
            this.name = name;
            this.email = email;
        }
    }


}
