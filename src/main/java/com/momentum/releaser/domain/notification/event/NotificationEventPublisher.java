package com.momentum.releaser.domain.notification.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationEventPublisher {

    private final ApplicationEventPublisher publisher;

    public void notifyReleaseNote(final ReleaseNoteMessageEvent releaseNoteEvent) {
        publisher.publishEvent(releaseNoteEvent);
    }

    public void notifyIssue(final IssueMessageEvent issueMessageEvent) {
        publisher.publishEvent(issueMessageEvent);
    }
}
