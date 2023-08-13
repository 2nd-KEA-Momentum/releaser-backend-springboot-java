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

    @GetMapping
    public BaseResponse<Page<NotificationListResponseDto>> notificationList(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PageableDefault(size = 5) Pageable pageable) {

        return new BaseResponse<>(notificationService.findNotificationList(userPrincipal.getEmail(), pageable));
    }

    @PostMapping
    public BaseResponse<String> notificationApproval(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestBody @Valid NotificationApprovalRequestDto notificationApprovalRequestDto) {

        return new BaseResponse<>(notificationService.sendApprovalNotification(userPrincipal.getEmail(), notificationApprovalRequestDto));
    }
}
