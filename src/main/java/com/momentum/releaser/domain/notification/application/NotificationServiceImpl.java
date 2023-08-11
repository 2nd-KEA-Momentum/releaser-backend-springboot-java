package com.momentum.releaser.domain.notification.application;

import com.momentum.releaser.domain.notification.dto.NotificationRequestDto.NotificationApprovalRequestDto;
import com.momentum.releaser.domain.notification.event.NotificationEventPublisher;
import com.momentum.releaser.domain.notification.event.ReleaseNoteMessageEvent;
import com.momentum.releaser.domain.project.dao.ProjectMemberRepository;
import com.momentum.releaser.domain.project.domain.Project;
import com.momentum.releaser.domain.release.dao.approval.ReleaseApprovalRepository;
import com.momentum.releaser.domain.release.dao.release.ReleaseRepository;
import com.momentum.releaser.domain.release.domain.ReleaseApproval;
import com.momentum.releaser.domain.release.domain.ReleaseEnum.ReleaseDeployStatus;
import com.momentum.releaser.domain.release.domain.ReleaseNote;
import com.momentum.releaser.global.exception.CustomException;
import com.momentum.releaser.rabbitmq.MessageDto.ReleaseNoteMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.momentum.releaser.global.config.BaseResponseStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final ProjectMemberRepository projectMemberRepository;
    private final ReleaseRepository releaseRepository;
    private final ReleaseApprovalRepository releaseApprovalRepository;
    private final NotificationEventPublisher notificationEventPublisher;

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
