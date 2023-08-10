package com.momentum.releaser.domain.notification.event;

import com.momentum.releaser.redis.notification.Notification;
import com.momentum.releaser.redis.notification.NotificationRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final RabbitTemplate rabbitTemplate;
    private final DirectExchange userDirectExchange;
    private final DirectExchange projectDirectExchange;

    private final NotificationRedisRepository notificationRedisRepository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onReleaseNoteEvent(final ReleaseNoteMessageEvent releaseNoteMessageEvent) {
        log.info("Received releaseNoteMessageEvent: {}", releaseNoteMessageEvent.toString());

        List<String> consumers = releaseNoteMessageEvent.getConsumers();

        if (releaseNoteMessageEvent.getType() == ReleaseNoteMessageEvent.ConsumerType.PROJECT) {
            // 알림 타입이 프로젝트인 경우 해당 프로젝트 큐로 메시지를 전송한다.
            String routingKey = "releaser.project." + releaseNoteMessageEvent.getMessage().getProjectId();
            rabbitTemplate.convertAndSend(projectDirectExchange.getName(), routingKey, releaseNoteMessageEvent.getMessage());
        }

        if (releaseNoteMessageEvent.getType() == ReleaseNoteMessageEvent.ConsumerType.USER) {
            // 알림 타입이 사용자인 경우 해당 사용자 개별 큐로 메시지를 전송한다.
            for (String consumer : consumers) {
                String routingKey = "releaser.user." + consumer;
                rabbitTemplate.convertAndSend(userDirectExchange.getName(), routingKey, releaseNoteMessageEvent.getMessage());
            }
        }

        // Redis에 필요한 값을 저장한다.
        saveReleaseNoteNotificationToRedis(releaseNoteMessageEvent);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onIssueEvent(final IssueMessageEvent issueMessageEvent) {
        log.info("Received: {}", issueMessageEvent.toString());
    }

    /**
     * 릴리즈 노트 알림 메시지와 필요한 정보들을 Redis에 저장한다.
     *
     * @author seonwoo
     * @date 2023-08-11 (금)
     * @param notificationEvent 알림 메시지 이벤트
     */
    private void saveReleaseNoteNotificationToRedis(ReleaseNoteMessageEvent notificationEvent) {
        // 사용자들의 알림 확인 여부를 체크하기 위해 데이터를 추가한다.
        HashMap<String, Integer> markByUsers = new HashMap<>();
        List<String> consumers = notificationEvent.getConsumers();
        for (String consumer : consumers) {
            markByUsers.put(consumer, 0);
        }

        // Redis에 저장하기 위한 데이터를 생성한다.
        Notification notification = Notification.builder()
                .notificationId(notificationEvent.getEventId())
                .type("Release Note")
                .projectTitle(notificationEvent.getMessage().getProjectName())
                .projectImg(notificationEvent.getMessage().getProjectImg())
                .message(notificationEvent.getMessage().getMessage())
                .date(notificationEvent.getMessage().getDate())
                .markByUsers(markByUsers)
                .expiredTime(604800) // 일주일
                .build();

        log.info("Notification to Redis: {}", notification.toString());

        notificationRedisRepository.save(notification);
    }
}
