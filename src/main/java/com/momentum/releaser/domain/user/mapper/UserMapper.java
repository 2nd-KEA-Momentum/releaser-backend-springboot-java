package com.momentum.releaser.domain.user.mapper;

import com.momentum.releaser.domain.user.domain.User;
import com.momentum.releaser.domain.user.dto.UserResponseDto.UserProfileImgResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    /**
     * Entity(User) -> DTO(UserProfileImgResponseDto)
     */
    UserProfileImgResponseDto toUserProfileImgResponseDto(User user);
}
