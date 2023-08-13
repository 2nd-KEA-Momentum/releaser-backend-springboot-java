package com.momentum.releaser.domain.notification.application;

import com.momentum.releaser.domain.notification.dto.NotificationRequestDto.NotificationApprovalRequestDto;
import com.momentum.releaser.domain.notification.dto.NotificationResponseDto.NotificationListResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface NotificationService {

    Page<NotificationListResponseDto> findNotificationList(String userEmail, Pageable pageable);

    /**
     * 11.2 릴리즈 노트 배포 동의 선택 알림
     *
     * @param userEmail                      사용자 이메일
     * @param notificationApprovalRequestDto 릴리즈 식별 번호가 담긴 DTO
     * @return 알림 전달 성공 메시지
     */
    String sendApprovalNotification(String userEmail, NotificationApprovalRequestDto notificationApprovalRequestDto);
}
