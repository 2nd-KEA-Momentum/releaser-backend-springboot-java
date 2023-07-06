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
import java.util.Optional;
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

        List<Issue> issues = getIssuesByReleaseNote(releaseCreateRequestDto);

        // 클라이언트로부터 전달받은 릴리즈 버전 타입을 바탕으로 올바른 릴리즈 버전을 생성한다.
        //
        String newVersion = "";

        // 데이터베이스로부터 가장 최신의 버전을 가져온다.
        Optional<ReleaseNote> optionalReleaseNote = releaseRepository.findTopByProject(project);

        if (optionalReleaseNote.isEmpty()) {
            // 데이터베이스에서 가장 최신의 버전을 가져오지 못한 경우

            int size = releaseRepository.findAllByProject(project).size();

            if (size != 0) {
                throw new CustomException(FAILED_TO_GET_LATEST_RELEASE_VERSION);
            } else {
                // 처음 생성하는 릴리즈 노트인 경우
                newVersion = "1.0.0";
            }
        } else {
            // 데이터베이스에서 가장 최신의 버전을 가져온 경우

            String latestVersion = optionalReleaseNote.get().getVersion();

            int latestMajorVersion = latestVersion.charAt(0) - 48;
            int latestMinorVersion = latestVersion.charAt(2) - 48;
            int latestPatchVersion = latestVersion.charAt(4) - 48;

            // 버전 종류에 따른 버전을 생성한다.
            String versionType = releaseCreateRequestDto.getVersionType();
            switch (versionType.toUpperCase()) {

                case "MAJOR":
                    int major = latestMajorVersion + 1;
                    newVersion = major + "." + latestMinorVersion + "." + latestPatchVersion;
                    break;

                case "MINOR":
                    int minor = latestMinorVersion + 1;
                    newVersion = latestMajorVersion + "." + minor + "." + latestPatchVersion;
                    break;

                case "PATCH":
                    int patch = latestPatchVersion + 1;
                    newVersion = latestMajorVersion + "." + latestMinorVersion + "." + patch;
                    break;

                default:
                    // 클라이언트로부터 받은 버전 타입이 올바르지 않은 경우 예외를 발생시킨다.
                    throw new CustomException(INVALID_RELEASE_VERSION_TYPE);
            }
        }

        // 새로운 릴리즈 노트 생성
        ReleaseNote newReleaseNote = ReleaseNote.builder()
                .title(releaseCreateRequestDto.getTitle())
                .content(releaseCreateRequestDto.getContent())
                .summary(releaseCreateRequestDto.getSummary())
                .version(newVersion)
                .deployDate(releaseCreateRequestDto.getDeployDate())
                .project(project)
                .build();

        // 릴리즈 노트 엔티티 저장
        ReleaseNote savedReleaseNote = releaseRepository.save(newReleaseNote);

        // 새롭게 생성된 릴리즈 노트에 이슈들을 연결시킨다.
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

    /**
     * 5.3 릴리즈 노트 수정
     */
    @Override
    public int updateReleaseNote(Long releaseId, ReleaseCreateRequestDto releaseCreateRequestDto) {
        ReleaseNote releaseNote = releaseRepository.findById(releaseId).orElseThrow(() -> new CustomException(NOT_EXISTS_RELEASE_NOTE));

        // 기존의 이슈들에 대해 연결을 해제한다.
        releaseNote.getIssues()
                .forEach(Issue::disconnectReleaseNote);

        // 새로 업데이트된 이슈들로 다시 연결한다.
        List<Issue> updatedIssues = getIssuesByReleaseNote(releaseCreateRequestDto);

        // 클라이언트로부터 전달받은 버전 타입을 바탕으로 올바른 버전을 생성한다.


        return 1;
    }

    /**
     * 연결할 이슈들의 식별 번호를 가지고 엔티티 형태로 받아온다.
     * 만약 없다면 예외를 발생시킨다.
     */
    private List<Issue> getIssuesByReleaseNote(ReleaseCreateRequestDto releaseCreateRequestDto) {
         return releaseCreateRequestDto.getIssues().stream()
                .map(i -> issueRepository.findById(i).orElseThrow(() -> new CustomException(NOT_EXISTS_ISSUE)))
                .collect(Collectors.toList());
    }
}
