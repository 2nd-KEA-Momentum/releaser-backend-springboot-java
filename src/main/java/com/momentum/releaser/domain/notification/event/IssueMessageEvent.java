package com.momentum.releaser.domain.notification.event;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.momentum.releaser.domain.notification.event.ReleaseNoteMessageEvent.ConsumerType;
import com.momentum.releaser.rabbitmq.MessageDto.IssueMessageDto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@Builder
@ToString
public class IssueMessageEvent {

    private String eventId;
    private ConsumerType type;
    private IssueMessageDto message;
    private List<String> consumers;

    /**
     * 이슈와 관련된 알림을 발생시킬 이벤트
     *
     * @param message 알림 메시지
     * @return IssueMessageEvent
     * @author seonwoo
     * @date 2023-08-09 (수)
     */
    public static IssueMessageEvent toNotifyAllIssue(final IssueMessageDto message, List<String> consumers) {
        return IssueMessageEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .message(message)
                .type(ConsumerType.PROJECT)
                .consumers(consumers)
                .build();
    }

    public static IssueMessageEvent toNotifyOneIssue(final IssueMessageDto message, String consumer) {
        List<String> consumers = new ArrayList<>();
        consumers.add(consumer);

        return IssueMessageEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .message(message)
                .type(ConsumerType.USER)
                .consumers(consumers)
                .build();
    }
}
