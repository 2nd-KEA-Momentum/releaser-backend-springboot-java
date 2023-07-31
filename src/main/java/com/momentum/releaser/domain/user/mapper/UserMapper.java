package com.momentum.releaser.domain.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.momentum.releaser.domain.user.domain.User;
import com.momentum.releaser.domain.user.dto.UserResponseDto.UserProfileImgResponseDTO;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    /**
     * Entity(User) -> DTO(UserProfileImgResponseDto)
     */
    UserProfileImgResponseDTO toUserProfileImgResponseDto(User user);
}
