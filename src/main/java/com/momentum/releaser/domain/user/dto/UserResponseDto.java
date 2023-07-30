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


}
