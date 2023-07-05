package com.momentum.releaser.domain.release.application;

import com.momentum.releaser.domain.issue.dao.IssueRepository;
import com.momentum.releaser.domain.issue.domain.Issue;
import com.momentum.releaser.domain.project.dao.ProjectRepository;
import com.momentum.releaser.domain.project.domain.Project;
import com.momentum.releaser.domain.release.dao.ReleaseRepository;
import com.momentum.releaser.domain.release.domain.ReleaseNote;
import com.momentum.releaser.domain.release.dto.ReleaseDataDto.ReleasesDataDto;
import com.momentum.releaser.domain.release.dto.ReleaseRequestDto.ReleaseCreateRequestDto;
import com.momentum.releaser.domain.release.dto.ReleaseResponseDto.ReleaseCreateResponseDto;
import com.momentum.releaser.domain.release.dto.ReleaseResponseDto.ReleasesResponseDto;
import com.momentum.releaser.domain.release.mapper.ReleaseMapper;
import com.momentum.releaser.global.error.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.momentum.releaser.global.config.BaseResponseStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReleaseServiceImpl implements ReleaseService {

    private final ProjectRepository projectRepository;
    private final ReleaseRepository releaseRepository;
    private final IssueRepository issueRepository;

    /**
     * 5.1 프로젝트별 릴리즈 노트 목록 조회
     */
    @Transactional(readOnly = true)
    @Override
    public ReleasesResponseDto getReleasesByProject(Long projectId) {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new CustomException(NOT_EXISTS_PROJECT));

        List<ReleaseNote> releases = releaseRepository.findAllByProject(project);
        List<ReleasesDataDto> releasesDataDtos = releases.stream()
                .map(ReleaseMapper.INSTANCE::toReleasesDataDto)
                .collect(Collectors.toList());

        return ReleasesResponseDto.builder()
                .projectId(project.getProjectId())
                .title(project.getTitle())
                .team(project.getTeam())
                .img(project.getImg())
                .releases(releasesDataDtos)
                .build();
    }

    /**
     * 5.2 릴리즈 노트 생성하기
     */
    @Transactional
    @Override
    public ReleaseCreateResponseDto createReleaseNote(Long projectId, ReleaseCreateRequestDto releaseCreateRequestDto) {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new CustomException(NOT_EXISTS_PROJECT));

        // 연결할 이슈들을 엔티티 형태로 받아온다. 만약 없다면 예외를 발생시킨다.
        List<Issue> issues = releaseCreateRequestDto.getIssues().stream()
                .map(i -> issueRepository.findById(i).orElseThrow(() -> new CustomException(NOT_EXISTS_ISSUE)))
                .collect(Collectors.toList());

        // 릴리즈 노트 생성 및 저장
        ReleaseNote newReleaseNote = ReleaseNote.builder()
                .title(releaseCreateRequestDto.getTitle())
                .content(releaseCreateRequestDto.getContent())
                .summary(releaseCreateRequestDto.getSummary())
                .version(releaseCreateRequestDto.getVersion())
                .deployDate(releaseCreateRequestDto.getDeployDate())
                .project(project)
                .build();

        ReleaseNote savedReleaseNote = releaseRepository.save(newReleaseNote);

        if (!Objects.equals(newReleaseNote.getReleaseId(), savedReleaseNote.getReleaseId())) {
            // 만약 데이터베이스에 제대로 저장이 되지 않은 경우 예외를 발생시킨다.
            throw new CustomException(FAILED_TO_CREATE_RELEASE_NOTE);
        }

        issues.forEach(i -> {
            i.updateReleaseNote(savedReleaseNote);
            Issue updatedIssue = issueRepository.save(i);

            if (!Objects.equals(i.getIssueId(), updatedIssue.getIssueId())) {
                // 만약 릴리즈 노트와 이슈의 연결이 제대로 업데이트 되지 않은 경우 예외를 발생시킨다.
                throw new CustomException(FAILED_TO_CONNECT_ISSUE_WITH_RELEASE_NOTE);
            }
        });

        return ReleaseMapper.INSTANCE.toReleaseCreateResponseDto(savedReleaseNote);
    }
}
