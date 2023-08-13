package com.momentum.releaser.domain.notification.application;

import com.momentum.releaser.domain.notification.dto.NotificationRequestDto.NotificationApprovalRequestDto;
import com.momentum.releaser.domain.notification.dto.NotificationResponseDto.NotificationListResponseDto;
import com.momentum.releaser.domain.notification.event.NotificationEventPublisher;
import com.momentum.releaser.domain.notification.event.ReleaseNoteMessageEvent;
import com.momentum.releaser.domain.notification.mapper.NotificationMapper;
import com.momentum.releaser.domain.project.dao.ProjectMemberRepository;
import com.momentum.releaser.domain.project.domain.Project;
import com.momentum.releaser.domain.release.dao.approval.ReleaseApprovalRepository;
import com.momentum.releaser.domain.release.dao.release.ReleaseRepository;
import com.momentum.releaser.domain.release.domain.ReleaseApproval;
import com.momentum.releaser.domain.release.domain.ReleaseEnum.ReleaseDeployStatus;
import com.momentum.releaser.domain.release.domain.ReleaseNote;
import com.momentum.releaser.global.exception.CustomException;
import com.momentum.releaser.rabbitmq.MessageDto.ReleaseNoteMessageDto;
import com.momentum.releaser.redis.notification.Notification;
import com.momentum.releaser.redis.notification.NotificationPerUser;
import com.momentum.releaser.redis.notification.NotificationPerUserRedisRepository;
import com.momentum.releaser.redis.notification.NotificationRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.expression.Lists;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.momentum.releaser.global.config.BaseResponseStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final ProjectMemberRepository projectMemberRepository;
    private final ReleaseRepository releaseRepository;
    private final ReleaseApprovalRepository releaseApprovalRepository;

    private final NotificationRedisRepository notificationRedisRepository;
    private final NotificationPerUserRedisRepository notificationPerUserRedisRepository;

    private final NotificationEventPublisher notificationEventPublisher;

    private final RedisTemplate<String, Notification> redisTemplate;

    @Override
    public Page<NotificationListResponseDto> findNotificationList(String userEmail, Pageable pageable) {
        // 사용자의 알림 내역 목록을 페이지네이션해서 가져온다.
        Page<Notification> notifications = findNotificationAllByUserEmail(userEmail, pageable);

        // 페이지네이션한 목록을 List<>로 변환해 반환한다.
        return mapToNotificationListResponseDto(userEmail, notifications, pageable);
    }

    @Transactional(readOnly = true)
    @Override
    public String sendApprovalNotification(String userEmail, NotificationApprovalRequestDto notificationApprovalRequestDto) {
        ReleaseNote releaseNote = findReleaseNoteById(notificationApprovalRequestDto);
        Project project = releaseNote.getProject();
        validateApprovalNotification(releaseNote);
        ReleaseNoteMessageDto message = createReleaseNoteMessage(project, releaseNote);
        List<String> consumers = findProjectMembersByProject(project);
        notificationEventPublisher.notifyReleaseNote(ReleaseNoteMessageEvent.toNotifyAllReleaseNote(message, consumers));
        return "릴리즈 노트 배포 동의 여부 알림이 전송되었습니다.";
    }

    // =================================================================================================================

    private Page<Notification> findNotificationAllByUserEmail(String userEmail, Pageable pageable) {
        NotificationPerUser notificationPerUser = notificationPerUserRedisRepository.findById(userEmail)
                .orElseThrow(() -> new CustomException(NOT_EXISTS_NOTIFICATION_PER_USER));

        List<String> notificationIds = notificationPerUser.getNotifications();
        Iterable<Notification> notificationIterable = notificationRedisRepository.findAllById(notificationIds);
        List<Notification> notifications = new ArrayList<>();
        notificationIterable.forEach(notifications::add);

        return createPageFromList(notifications, pageable);
    }

    private Page<Notification> createPageFromList(List<Notification> notifications, Pageable pageable) {
        int startIdx = (int) pageable.getOffset();
        int endIdx = Math.min(startIdx + pageable.getPageSize(), notifications.size());
        List<Notification> notificationsAfterPaging = notifications.subList(startIdx, endIdx);
        return new PageImpl<>(notificationsAfterPaging, pageable, notifications.size());
    }

    private PageImpl<NotificationListResponseDto> mapToNotificationListResponseDto(String userEmail, Page<Notification> notifications, Pageable pageable) {
        List<NotificationListResponseDto> notificationDtos = notifications.stream()
                .map(NotificationMapper.INSTANCE::toNotificationListResponseDto)
                .collect(Collectors.toList());

        HashOperations<String, String, Map<String, Integer>> hashOperations = redisTemplate.opsForHash();

        for (NotificationListResponseDto notificationDto : notificationDtos) {
            Notification notification = notificationRedisRepository.findById(notificationDto.getNotificationId())
                    .orElseThrow(() -> new CustomException(NOT_EXISTS_NOTIFICATION_PER_USER));

            Map<String, Integer> markByUsers = notification.getMarkByUsers();

            if (markByUsers == null) {
                throw new CustomException(NOT_EXISTS_USERS_IN_NOTIFICATION_DATA);
            }

            Integer isRead = markByUsers.get(userEmail);
            notificationDto.updateIsRead(isRead == null ? 0 : isRead);
        }

        return new PageImpl<>(notificationDtos, pageable, notifications.getTotalElements());
    }

    private ReleaseNote findReleaseNoteById(NotificationApprovalRequestDto notificationApprovalRequestDto) {
        Long releaseId = notificationApprovalRequestDto.getReleaseId();
        return releaseRepository.findById(releaseId).orElseThrow(() -> new CustomException(NOT_EXISTS_RELEASE_NOTE));
    }

    private void validateApprovalNotification(ReleaseNote releaseNote) {
        // 만약 이미 배포가 된 릴리즈 노트라면 알림을 전송할 수 없다.
        if (releaseNote.getDeployStatus() == ReleaseDeployStatus.DEPLOYED) {
            throw new CustomException(ALREADY_DEPLOYED_RELEASE_NOTE);
        }

        // 만약 이미 모든 멤버들의 동의가 완료되었다면 알림을 전송할 수 없다.
        List<ReleaseApproval> approvals = releaseApprovalRepository.findAllByRelease(releaseNote);
        int yesCount = 0;

        for (ReleaseApproval approval : approvals) {
            if (approval.getApproval() == 'Y') {
                yesCount++;
            }
        }

        if (yesCount == approvals.size()) {
            throw new CustomException(ALREADY_ALL_APPROVALS_WITH_YES);
        }
    }

    private ReleaseNoteMessageDto createReleaseNoteMessage(Project project, ReleaseNote releaseNote) {
        return ReleaseNoteMessageDto.builder()
                .projectId(project.getProjectId())
                .projectName(project.getTitle())
                .projectImg(project.getImg())
                .message("릴리즈 노트의 배포 동의 여부를 선택해 주세요.")
                .date(Date.from(releaseNote.getCreatedDate().atZone(ZoneId.systemDefault()).toInstant()))
                .releaseNoteId(releaseNote.getReleaseId())
                .build();
    }

    private List<String> findProjectMembersByProject(Project project) {
        return projectMemberRepository.findByProject(project).stream()
                .map(m -> m.getUser().getEmail())
                .collect(Collectors.toList());
    }
}
