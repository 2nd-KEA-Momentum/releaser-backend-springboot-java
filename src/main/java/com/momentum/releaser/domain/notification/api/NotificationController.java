package com.momentum.releaser.domain.notification.api;

import com.momentum.releaser.domain.notification.application.NotificationService;
import com.momentum.releaser.domain.notification.dto.NotificationRequestDto;
import com.momentum.releaser.domain.notification.dto.NotificationRequestDto.NotificationApprovalRequestDto;
import com.momentum.releaser.global.config.BaseResponse;
import com.momentum.releaser.global.jwt.UserPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Validated
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping()
    public BaseResponse<String> notificationApproval(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody @Valid NotificationApprovalRequestDto notificationApprovalRequestDto) {

        return new BaseResponse<>(notificationService.sendApprovalNotification(userPrincipal.getEmail(), notificationApprovalRequestDto));
    }
}
