package com.momentum.releaser.domain.notification.mapper;

import com.momentum.releaser.domain.notification.dto.NotificationResponseDto.NotificationListResponseDto;
import com.momentum.releaser.redis.notification.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface NotificationMapper {

    NotificationMapper INSTANCE = Mappers.getMapper(NotificationMapper.class);

    NotificationListResponseDto toNotificationListResponseDto(Notification notification);
}
