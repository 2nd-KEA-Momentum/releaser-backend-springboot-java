package com.momentum.releaser.domain.release.application;

import com.momentum.releaser.domain.issue.dao.IssueRepository;
import com.momentum.releaser.domain.issue.domain.Issue;
import com.momentum.releaser.domain.issue.domain.LifeCycle;
import com.momentum.releaser.domain.project.dao.ProjectRepository;
import com.momentum.releaser.domain.project.domain.Project;
import com.momentum.releaser.domain.release.dao.ReleaseRepository;
import com.momentum.releaser.domain.release.domain.ReleaseNote;
import com.momentum.releaser.domain.release.dto.ReleaseDataDto.ReleasesDataDto;
import com.momentum.releaser.domain.release.dto.ReleaseRequestDto.ReleaseCreateRequestDto;
import com.momentum.releaser.domain.release.dto.ReleaseRequestDto.ReleaseUpdateRequestDto;
import com.momentum.releaser.domain.release.dto.ReleaseResponseDto.ReleaseCreateResponseDto;
import com.momentum.releaser.domain.release.dto.ReleaseResponseDto.ReleasesResponseDto;
import com.momentum.releaser.domain.release.mapper.ReleaseMapper;
import com.momentum.releaser.global.error.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
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
     * 5.2 릴리즈 노트 생성
     */
    @Transactional
    @Override
    public ReleaseCreateResponseDto createReleaseNote(Long projectId, ReleaseCreateRequestDto releaseCreateRequestDto) {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new CustomException(NOT_EXISTS_PROJECT));

        // 연결할 이슈들의 식별 번호를 가지고 엔티티 형태로 받아온다.
        List<Issue> issues = releaseCreateRequestDto.getIssues().stream()
                .map(i -> issueRepository.findById(i).orElseThrow(() -> new CustomException(NOT_EXISTS_ISSUE)))
                .collect(Collectors.toList());

        // 클라이언트로부터 전달받은 릴리즈 버전 타입을 바탕으로 올바른 릴리즈 버전을 생성한다.

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
            issueRepository.save(i);
        });

        return ReleaseMapper.INSTANCE.toReleaseCreateResponseDto(savedReleaseNote);
    }

    /**
     * 5.3 릴리즈 노트 수정
     */
    @Transactional
    @Override
    public int updateReleaseNote(Long releaseId, ReleaseUpdateRequestDto releaseUpdateRequestDto) {
        ReleaseNote releaseNote = releaseRepository.findById(releaseId).orElseThrow(() -> new CustomException(NOT_EXISTS_RELEASE_NOTE));

        // 기존의 이슈들에 대해 연결을 해제한다.
        releaseNote.getIssues()
                .forEach(Issue::disconnectReleaseNote);

        // 클라이언트로부터 전달받은 버전이 올바른지 검사한다.
        String newVersion = releaseUpdateRequestDto.getVersion();

        // 1. 만약 수정하려고 하는 릴리즈 노트의 원래 버전이 1.0.0인 경우 수정하지 못하도록 한다. 이 경우 릴리즈 노트 내용만 수정해야 한다.
        if (Objects.equals(releaseNote.getVersion(), "1.0.0")) {
            throw new CustomException(FAILED_TO_UPDATE_INITIAL_RELEASE_VERSION);
        }

        // 2. 중복된 버전이 있는지 확인한다. 중복된 버전이 존재할 경우 예외를 발생시킨다.
        if (releaseRepository.existsByVersion(newVersion)) {
            throw new CustomException(DUPLICATED_RELEASE_VERSION);
        }

        // 3. 해당 프로젝트의 모든 릴리즈 버전을 가져온 후, 변경하려는 버전을 이어 붙인다.
        List<String> versions = releaseRepository.findByProjectAndNotInVersion(releaseNote.getProject(), releaseNote.getVersion()).stream().map(ReleaseNote::getVersion).collect(Collectors.toList());
        versions.add(newVersion);

        // 4. 변경하려는 버전이 포함된 릴리즈 버전 배열을 오름차순으로 정렬한다.
        Collections.sort(versions);

        // 5. 바꾸려는 버전 값이 올바른 버전 값인지를 확인한다.
        validateCorrectVersion(versions);

        // 릴리즈 노트르루 수정한 후 저장한다.
        releaseNote.updateReleaseNote(
                releaseUpdateRequestDto.getTitle(),
                releaseUpdateRequestDto.getContent(),
                releaseUpdateRequestDto.getSummary(),
                newVersion,
                releaseUpdateRequestDto.getDeployDate()
        );
        ReleaseNote savedReleaseNote = releaseRepository.save(releaseNote);

        // 클라이언트로부터 전달받은 이슈 식별 번호 목록을 통해 이슈 엔티티 목록을 가져온다.
        List<Issue> updatedIssues = releaseUpdateRequestDto.getIssues().stream()
                .map(i -> issueRepository.findById(i).orElseThrow(() -> new CustomException(NOT_EXISTS_ISSUE)))
                .collect(Collectors.toList());

        // 새로 업데이트된 이슈들로 다시 연결한다.
        updatedIssues.forEach(i -> {
            // 각각의 이슈들에 이미 연결된 릴리즈 노트가 없는지, 각 이슈들은 완료된 상태인지를 한 번 더 확인한다.
            if (i.getLifeCycle() == LifeCycle.Completed || i.getRelease() != null) {
                throw new CustomException(INVALID_ISSUE_WITH_COMPLETED);
            }

            if (i.getLifeCycle() != LifeCycle.Done) {
                throw new CustomException(INVALID_ISSUE_WITH_NOT_DONE);
            }

            // 문제가 없는 경우 연결한다.
            i.updateReleaseNote(savedReleaseNote);
            issueRepository.save(i);
        });

        return 1;
    }

    /**
     * 클라이언트가 수정하고자 하는 버전이 올바른 버전인지 검증한다.
     */
    private void validateCorrectVersion(List<String> versions) {
        int[] majors = versions.stream().mapToInt(v -> v.charAt(0) - 48).toArray();
        int[] minors = versions.stream().mapToInt(v -> v.charAt(2) - 48).toArray();
        int[] patches = versions.stream().mapToInt(v -> v.charAt(4) - 48).toArray();

        int majorStartIdx = 0;
        int minorStartIdx = 0;

        validateMajorVersion(majors, minors, patches, versions.size() - 1, majorStartIdx, minorStartIdx);
    }

    /**
     * Major(메이저) 버전 숫자에 대한 유효성 검사를 진행한다.
     */
    private void validateMajorVersion(int[] majors, int[] minors, int[] patches, int end, int majorStartIdx, int minorStartIdx) {

        for (int i = 0; i < end; i++) {
            int currentMajor = majors[i];
            int nextMajor = majors[i + 1];

            // 만약 연속되는 두 개의 메이저 버전 숫자가 +-1이 아닌 경우 예외를 발생시킨다.
            if ((nextMajor - currentMajor > 1) || (nextMajor - currentMajor < 0)) {
                throw new CustomException(INVALID_RELEASE_VERSION);
            }

            // 만약 가장 큰 메이저 버전 숫자인 경우 해당 메이저 버전에 대한 모든 하위 버전의 유효성 검사를 진행한다.
            if (currentMajor == nextMajor && i + 1 == end) {
                validateMinorVersion(minors, patches, majorStartIdx, end, minorStartIdx);
                return;
            }

            // 만약 그 다음 번째 메이저 버전 숫자가 바뀌는 경우 넘어가기 전에 마이너 버전 숫자를 확인한다.
            if (nextMajor - currentMajor == 1) {
                validateMinorVersion(minors, patches, majorStartIdx, i, minorStartIdx);
                majorStartIdx = i + 1;

                // 메이저 버전 숫자가 바뀌었을 때 마이너와 패치 버전 숫자는 모두 0이어야 한다.
                if (minors[majorStartIdx] != 0 || patches[majorStartIdx] != 0) {
                    throw new CustomException(INVALID_RELEASE_VERSION);
                }
            }
        }
    }

    /**
     * Minor(마이너) 버전 숫자에 대한 유효성 검사를 진행한다.
     */
    private void validateMinorVersion(int[] minors, int[] patches, int start, int end, int minorStartIdx) {

        if (end == 0) {
            return;
        }

        for (int i = start; i < end; i++) {
            int currentMinor = minors[i];
            int nextMinor = minors[i + 1];

            // 만약 연속되는 두 개의 마이너 버전 숫자가 +-1이 아닌 경우 예외를 발생시킨다.
            if ((nextMinor - currentMinor > 1) || (nextMinor - currentMinor < 0)) {
                throw new CustomException(INVALID_RELEASE_VERSION);
            }

            // 만약 가장 큰 마이너 버전 숫자인 경우 해당 마이너 버전에 대한 모든 하위 버전의 유효성 검사를 진행한다.
            if (currentMinor == nextMinor && i + 1 == end) {
                validatePatchVersion(patches, minorStartIdx, end);
                return;
            }

            // 만약 그 다음 번째 마이너 버전 숫자가 바뀌는 경우 넘어가기 전에 패치 버전 숫자를 확인한다.
            if (nextMinor - currentMinor == 1) {
                validatePatchVersion(patches, minorStartIdx, i + 1);
                minorStartIdx = i + 1;

                // 마이너 버전 숫자가 바뀌었을 때 패치 버전 숫자는 0이어야 한다.
                if (patches[minorStartIdx] != 0) {
                    throw new CustomException(INVALID_RELEASE_VERSION);
                }
            }
        }
    }

    /**
     * Patch(패치) 버전 숫자에 대한 유효성 검사를 진행한다.
     */
    private void validatePatchVersion(int[] patches, int start, int end) {

        if (end == 0) {
            return;
        }

        for (int i = start; i < end; i++) {
            int currentPatch = patches[i];
            int nextPatch = patches[i + 1];

            // 만약 연속되는 두 개의 메이저 버전 숫자가 +-1이 아닌 경우 예외를 발생시킨다.
            if ((nextPatch - currentPatch > 1) || (nextPatch - currentPatch < 0)) {
                throw new CustomException(INVALID_RELEASE_VERSION);
            }
        }
    }
}
