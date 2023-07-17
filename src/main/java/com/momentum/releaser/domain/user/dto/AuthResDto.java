package com.momentum.releaser.domain.user.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class AuthResDto {

    /**
     * 회원가입
     */
    @Data
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class UserInfoRes {
        private Long userId;
        private String name;
        private String email;

        @Builder
        public UserInfoRes(Long userId, String name, String email) {
            this.userId = userId;
            this.name = name;
            this.email = email;
        }
    }

    /**
     * 로그인
     */


}
