package com.momentum.releaser.domain.notification.event;

import java.util.UUID;

import com.momentum.releaser.rabbitmq.MessageDto.IssueMessageDto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class IssueMessageEvent {

    private String eventId;
    private IssueMessageDto message;

    /**
     * 이슈와 관련된 알림을 발생시킬 이벤트
     *
     * @param message 알림 메시지
     * @return IssueMessageEvent
     * @author seonwoo
     * @date 2023-08-09 (수)
     */
    public static IssueMessageEvent toNotifyIssue(final IssueMessageDto message) {
        return IssueMessageEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .message(message)
                .build();
    }
}
