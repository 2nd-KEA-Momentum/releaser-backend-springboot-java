package com.momentum.releaser.domain.notification.api;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.momentum.releaser.domain.notification.application.NotificationService;
import com.momentum.releaser.domain.notification.dto.NotificationRequestDto.NotificationApprovalRequestDto;
import com.momentum.releaser.domain.notification.dto.NotificationResponseDto.NotificationListResponseDto;
import com.momentum.releaser.global.config.BaseResponse;
import com.momentum.releaser.global.jwt.UserPrincipal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Validated
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * 11.1 사용자별 알림 내역 조회
     *
     * @param userPrincipal JWT, 사용자 이메일
     * @param pageable      페이징을 위한 정보 (예: page, size 등)
     * @return 사용자별 알림 내역 목록
     * @author seonwoo
     * @date 2023-08-14 (월)
     */
    @GetMapping
    public BaseResponse<Page<NotificationListResponseDto>> notificationList(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PageableDefault(size = 5) Pageable pageable) {

        return new BaseResponse<>(notificationService.findNotificationList(userPrincipal.getEmail(), pageable));
    }

    /**
     * 11.2 릴리즈 노트 배포 동의 알림
     *
     * @param userPrincipal                  JWT, 사용자 이메일
     * @param notificationApprovalRequestDto 릴리즈 식별 번호
     * @return 릴리즈 노트 패보 동의 알림 전송 성공 메시지
     * @author seonwoo
     * @date 2023-08-14 (월)
     */
    @PostMapping
    public BaseResponse<String> notificationApproval(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody @Valid NotificationApprovalRequestDto notificationApprovalRequestDto) {

        return new BaseResponse<>(notificationService.sendApprovalNotification(userPrincipal.getEmail(), notificationApprovalRequestDto));
    }
}
