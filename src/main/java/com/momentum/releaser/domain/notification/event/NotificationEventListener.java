package com.momentum.releaser.domain.notification.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final RabbitTemplate rabbitTemplate;
    private final DirectExchange userDirectExchange;
    private final DirectExchange projectDirectExchange;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onReleaseNoteEvent(final ReleaseNoteMessageEvent releaseNoteMessageEvent) {
        log.info("Received: {}", releaseNoteMessageEvent.toString());

        List<String> consumers = releaseNoteMessageEvent.getConsumers();

        if (releaseNoteMessageEvent.getType() == ReleaseNoteMessageEvent.ConsumerType.PROJECT) {

            for (String consumer : consumers) {
                String routingKey = "releaser.project." + consumer;
                rabbitTemplate.convertAndSend(projectDirectExchange.getName(), routingKey, releaseNoteMessageEvent.getMessage());
            }
        }

        if (releaseNoteMessageEvent.getType() == ReleaseNoteMessageEvent.ConsumerType.USER) {

            for (String consumer : consumers) {
                String routingKey = "releaser.user." + consumer;
                rabbitTemplate.convertAndSend(userDirectExchange.getName(), routingKey, releaseNoteMessageEvent.getMessage());
            }
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onIssueEvent(final IssueMessageEvent issueMessageEvent) {
        log.info("Received: {}", issueMessageEvent.toString());
    }
}
