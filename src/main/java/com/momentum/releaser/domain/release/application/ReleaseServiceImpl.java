package com.momentum.releaser.domain.release.application;

import com.momentum.releaser.domain.issue.dao.IssueRepository;
import com.momentum.releaser.domain.issue.domain.Issue;
import com.momentum.releaser.domain.issue.domain.LifeCycle;
import com.momentum.releaser.domain.project.dao.ProjectMemberRepository;
import com.momentum.releaser.domain.project.dao.ProjectRepository;
import com.momentum.releaser.domain.project.domain.Project;
import com.momentum.releaser.domain.project.domain.ProjectMember;
import com.momentum.releaser.domain.project.mapper.ProjectMapper;
import com.momentum.releaser.domain.release.dao.approval.ReleaseApprovalRepository;
import com.momentum.releaser.domain.release.dao.opinion.ReleaseOpinionRepository;
import com.momentum.releaser.domain.release.dao.release.ReleaseRepository;
import com.momentum.releaser.domain.release.domain.ReleaseApproval;
import com.momentum.releaser.domain.release.domain.ReleaseEnum.ReleaseDeployStatus;
import com.momentum.releaser.domain.release.domain.ReleaseNote;
import com.momentum.releaser.domain.release.domain.ReleaseOpinion;
import com.momentum.releaser.domain.release.dto.ReleaseDataDto.CoordinateDataDto;
import com.momentum.releaser.domain.release.dto.ReleaseRequestDto.*;
import com.momentum.releaser.domain.release.dto.ReleaseResponseDto.*;
import com.momentum.releaser.domain.release.mapper.ReleaseMapper;
import com.momentum.releaser.domain.user.dao.UserRepository;
import com.momentum.releaser.domain.user.domain.User;
import com.momentum.releaser.global.error.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.momentum.releaser.global.config.BaseResponseStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReleaseServiceImpl implements ReleaseService {

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final ReleaseRepository releaseRepository;
    private final ReleaseOpinionRepository releaseOpinionRepository;
    private final ReleaseApprovalRepository releaseApprovalRepository;
    private final IssueRepository issueRepository;

    /**
     * 5.1 프로젝트별 릴리즈 노트 목록 조회
     */
    @Transactional(readOnly = true)
    @Override
    public ReleasesResponseDto getReleasesByProject(String userEmail, Long projectId) {
        // 프로젝트 식별 번호로 프로젝트 엔티티를 가져온다.
        Project project = getProjectById(projectId);

        // 요청을 한 사용자의 프로젝트 내 역할을 가져올 수 있도록 한다.
        ProjectMember member = getProjectMemberByEmail(project, userEmail);

        return ProjectMapper.INSTANCE.toReleasesResponseDto(project, member);
    }

    /**
     * 5.2 릴리즈 노트 생성
     */
    @Transactional
    @Override
    public ReleaseCreateAndUpdateResponseDto createReleaseNote(Long projectId, ReleaseCreateRequestDto releaseCreateRequestDto) {
        // 먼저, 클라이언트로부터 받아온 릴리즈 노트를 저장한다.
        ReleaseNote savedReleaseNote = saveReleaseNote(projectId, releaseCreateRequestDto, createReleaseVersion(projectId, releaseCreateRequestDto.getVersionType()));

        // 이슈들을 연결한다.
        connectIssues(releaseCreateRequestDto.getIssues(), savedReleaseNote);

        // 생성된 릴리즈 노트에 대한 알림을 보낸다.
        alertCreatedReleaseNote(projectId, savedReleaseNote.getReleaseId());

        // 생성한 릴리즈 노트에 대한 동의 테이블을 생성한다.
        createReleaseApprovals(savedReleaseNote);

        return ReleaseMapper.INSTANCE.toReleaseCreateAndUpdateResponseDto(savedReleaseNote);
    }

    /**
     * 5.3 릴리즈 노트 수정
     */
    @Transactional
    @Override
    public ReleaseCreateAndUpdateResponseDto updateReleaseNote(Long releaseId, ReleaseUpdateRequestDto releaseUpdateRequestDto) {
        // 수정된 릴리즈 노트 내용을 반영 및 저장한다.
        ReleaseNote updatedReleaseNote = updateAndSaveReleaseNote(releaseId, releaseUpdateRequestDto, updateReleaseVersion(releaseId, releaseUpdateRequestDto.getVersion()));

        // 이슈를 연결한다.
        connectIssues(releaseUpdateRequestDto.getIssues(), updatedReleaseNote);

        // 배포 상태에 따른 알림을 보낸다.
        alertReleaseNoteDeploy(updatedReleaseNote);

        return ReleaseMapper.INSTANCE.toReleaseCreateAndUpdateResponseDto(updatedReleaseNote);
    }

    /**
     * 5.4 릴리즈 노트 삭제
     */
    @Transactional
    @Override
    public String deleteReleaseNote(Long releaseId) {
        // 해당 릴리즈 노트 삭제가 가능한지 확인한다.
        ReleaseNote releaseNote = getReleaseNoteById(releaseId);
        validateReleaseNoteDelete(releaseNote);

        // 해당 릴리즈 노트에 대한 배포 동의 여부 데이터를 모두 삭제한다.
        releaseApprovalRepository.deleteByReleaseNote(releaseNote);

        // 해당 릴리즈 노트를 삭제한다.
        releaseRepository.deleteById(releaseId);

        return "릴리즈 노트 삭제에 성공하였습니다.";
    }

    /**
     * 5.5 릴리즈 노트 조회
     */
    @Transactional(readOnly = true)
    @Override
    public ReleaseInfoResponseDto getReleaseNoteInfo(Long releaseId) {
        ReleaseNote releaseNote = getReleaseNoteById(releaseId);
        return ReleaseMapper.INSTANCE.toReleaseInfoResponseDto(releaseNote);
    }

    /**
     * 5.6 릴리즈 노트 배포 동의 여부 선택 (멤버용)
     */
    @Transactional
    @Override
    public List<ReleaseApprovalsResponseDto> decideOnApprovalByMember(Long releaseId, ReleaseApprovalRequestDto releaseApprovalRequestDto) {
        ReleaseNote releaseNote = getReleaseNoteById(releaseId);
        ProjectMember projectMember = getProjectMemberById(releaseApprovalRequestDto.getMemberId());

        // 배포 동의 여부를 선택할 수 있는 릴리즈인지 확인한다.
        validateReleaseNoteApproval(projectMember, releaseNote);

        // 릴리즈 노트에 대한 배포 동의 여부를 업데이트한다.
        updateReleaseNoteApproval(projectMember, releaseNote, releaseApprovalRequestDto.getApproval().charAt(0));

        // 프로젝트 멤버들의 업데이트된 동의 여부 목록을 반환한다.
        return getReleaseApprovals(releaseNote);
    }

    /**
     * 5.7 릴리즈 노트 그래프 좌표 추가
     */
    @Transactional
    @Override
    public String updateReleaseNoteCoordinate(ReleaseNoteCoordinateRequestDto releaseNoteCoordinateRequestDto) {
        updateCoordinates(releaseNoteCoordinateRequestDto.getCoordinates());
        return "릴리즈 노트 좌표 업데이트에 성공하였습니다.";
    }

    /**
     * 6.1 릴리즈 노트 의견 추가
     */
    @Transactional
    @Override
    public ReleaseOpinionCreateResponseDto addReleaseOpinion(Long releaseId, ReleaseOpinionCreateRequestDto releaseOpinionCreateRequestDto) {
        ReleaseNote releaseNote = getReleaseNoteById(releaseId);
        ProjectMember projectMember = getProjectMemberById(releaseOpinionCreateRequestDto.getMemberId());
        ReleaseOpinion savedReleaseOpinion = saveReleaseOpinion(releaseNote, projectMember, releaseOpinionCreateRequestDto);
        return ReleaseMapper.INSTANCE.toReleaseOpinionCreateResponseDto(savedReleaseOpinion);
    }

    /**
     * 6.2 릴리즈 노트 의견 삭제
     */
    @Transactional
    @Override
    public String deleteReleaseOpinion(Long opinionId) {
        releaseOpinionRepository.deleteById(opinionId);
        return "릴리즈 노트 의견 삭제에 성공하였습니다.";
    }

    /**
     * 6.3 릴리즈 노트 의견 목록 조회
     */
    @Transactional(readOnly = true)
    @Override
    public List<ReleaseOpinionsResponseDto> getReleaseOpinions(Long releaseId) {
        ReleaseNote releaseNote = getReleaseNoteById(releaseId);
        return getReleaseOpinionsResponseDto(releaseNote.getReleaseOpinions());
    }

    // =================================================================================================================

    /**
     * 프로젝트 식별 번호를 통해 프로젝트 엔티티를 가져온다.
     */
    private Project getProjectById(Long projectId) {
        return projectRepository.findById(projectId).orElseThrow(() -> new CustomException(NOT_EXISTS_PROJECT));
    }

    /**
     * 프로젝트 멤버 식별 번호를 통해 프로젝트 멤버 엔티티를 가져온다.
     */
    private ProjectMember getProjectMemberById(Long memberId) {
        return projectMemberRepository.findById(memberId).orElseThrow(() -> new CustomException(NOT_EXISTS_PROJECT_MEMBER));
    }

    /**
     * 릴리즈 식별 번호를 통해 릴리즈 엔티티를 가져온다.
     */
    private ReleaseNote getReleaseNoteById(Long releaseId) {
        return releaseRepository.findById(releaseId).orElseThrow(() -> new CustomException(NOT_EXISTS_RELEASE_NOTE));
    }

    /**
     * 프로젝트 조회 시 해당 요청을 한 사용자의 프로젝트 멤버의 역할을 알려주기 위해 프로젝트 멤버 엔티티를 가져온다.
     */
    private ProjectMember getProjectMemberByEmail(Project project, String email) {
        // 사용자 이메일을 통해 사용자 엔티티를 가져온다.
        User user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException(NOT_EXISTS_USER));

        // 사용자 엔티티를 통해 프로젝트 멤버 엔티티를 가져온다.
        return projectMemberRepository.findOneByUserAndProject(user, project).orElseThrow(() -> new CustomException(NOT_EXISTS_PROJECT_MEMBER));
    }

    /**
     * 이슈 식별 번호를 통해 이슈 엔티티 목록을 가져온다.
     */
    private List<Issue> getIssuesById(List<Long> issues) {
        return issues.stream()
                .map(i -> issueRepository.findById(i).orElseThrow(() -> new CustomException(NOT_EXISTS_ISSUE)))
                .collect(Collectors.toList());
    }

    /**
     * 릴리즈 버전 타입을 통해 올바른 릴리즈 버전을 생성한다.
     */
    private String createReleaseVersion(Long projectId, String versionType) {
        Project project = getProjectById(projectId);
        String newVersion = "";

        // 데이터베이스로부터 가장 최신의 버전을 가져온다.
//        Optional<ReleaseNote> optionalReleaseNote = releaseRepository.findLatestVersionByProject(project);
        List<String> releaseVersions = releaseRepository.findAllVersionsByProject(project);

        if (releaseVersions.isEmpty()) {  // 데이터베이스에서 가장 최신의 버전을 가져오지 못한 경우
            int size = releaseRepository.findAllByProject(project).size();

            if (size != 0) {
                throw new CustomException(FAILED_TO_GET_LATEST_RELEASE_VERSION);
            } else {  // 처음 생성하는 릴리즈 노트인 경우
                newVersion = "1.0.0";
            }

        } else {  // 데이터베이스에서 가장 최신의 버전을 가져온 경우
            releaseVersions = getLatestVersion(releaseVersions);
            String latestVersion = releaseVersions.get(0);

            String[] eachVersion = latestVersion.split("\\.");
            int latestMajorVersion = Integer.parseInt(eachVersion[0]);
            int latestMinorVersion = Integer.parseInt(eachVersion[1]);
            int latestPatchVersion = Integer.parseInt(eachVersion[2]);

            // 버전 종류에 따른 버전을 생성한다.
            switch (versionType.toUpperCase()) {
                case "MAJOR":
                    newVersion = (latestMajorVersion + 1) + ".0.0";
                    break;

                case "MINOR":
                    newVersion = latestMajorVersion + "." + (latestMinorVersion + 1) + ".0";
                    break;

                case "PATCH":
                    newVersion = latestMajorVersion + "." + latestMinorVersion + "." + (latestPatchVersion + 1);
                    break;

                default:
                    // 클라이언트로부터 받은 버전 타입이 올바르지 않은 경우 예외를 발생시킨다.
                    throw new CustomException(INVALID_RELEASE_VERSION_TYPE);
            }
        }
        return newVersion;
    }

    /**
     * 버전을 내림차순으로 정렬한다. 이렇게 되면 가장 첫 번째 요소가 제일 큰/최신 버전이 된다.
     */
    private List<String> getLatestVersion(List<String> versions) {
        versions.sort(new Comparator<String>() {
            @Override
            public int compare(String v1, String v2) {
                String[] v1s = v1.split("\\.");
                String[] v2s = v2.split("\\.");

                int majorV1 = Integer.parseInt(v1s[0]);
                int minorV1 = Integer.parseInt(v1s[1]);
                int patchV1 = Integer.parseInt(v1s[2]);

                int majorV2 = Integer.parseInt(v2s[0]);
                int minorV2 = Integer.parseInt(v2s[1]);
                int patchV2 = Integer.parseInt(v2s[2]);

                if (majorV1 != majorV2) {
                    return Integer.compare(majorV2, majorV1);
                } else if (minorV1 != minorV2) {
                    return Integer.compare(minorV2, minorV1);
                } else {
                    return Integer.compare(patchV2, patchV1);
                }
            }
        });

        return versions;
    }

    private List<String> sortVersionByAsc(List<String> versions) {

        versions.sort((v1, v2) -> {
            String[] v1s = v1.split("\\.");
            String[] v2s = v2.split("\\.");

            int majorV1 = Integer.parseInt(v1s[0]);
            int minorV1 = Integer.parseInt(v1s[1]);
            int patchV1 = Integer.parseInt(v1s[2]);

            int majorV2 = Integer.parseInt(v2s[0]);
            int minorV2 = Integer.parseInt(v2s[1]);
            int patchV2 = Integer.parseInt(v2s[2]);

            if (majorV1 != majorV2) {
                return Integer.compare(majorV1, majorV2);
            } else if (minorV1 != minorV2) {
                return Integer.compare(minorV1, minorV2);
            } else {
                return Integer.compare(patchV1, patchV2);
            }
        });

        return versions;
    }

    /**
     * 릴리즈 노트 엔티티 객체를 생성한 후, 데이터베이스에 저장한다.
     */
    private ReleaseNote saveReleaseNote(Long projectId, ReleaseCreateRequestDto releaseCreateRequestDto, String newVersion) {
        Project project = getProjectById(projectId);

        // 새로운 릴리즈 노트 생성
        ReleaseNote newReleaseNote = ReleaseNote.builder()
                .title(releaseCreateRequestDto.getTitle())
                .content(releaseCreateRequestDto.getContent())
                .summary(releaseCreateRequestDto.getSummary())
                .version(newVersion)
                .project(project)
                .coordX(releaseCreateRequestDto.getCoordX())
                .coordY(releaseCreateRequestDto.getCoordY())
                .build();

        // 릴리즈 노트 엔티티 저장
        return releaseRepository.save(newReleaseNote);
    }

    /**
     * 릴리즈 노트 엔티티를 업데이트(수정)한다.
     */
    private ReleaseNote updateAndSaveReleaseNote(Long releaseId, ReleaseUpdateRequestDto releaseUpdateRequestDto, String updatedVersion) {
        ReleaseNote releaseNote = getReleaseNoteById(releaseId);

        // 수정이 가능한 릴리즈 노트인지 유효성 검사를 진행한다.
        validateReleaseNoteUpdate(releaseNote, releaseUpdateRequestDto);

        // 먼저 연결된 이슈를 모두 해제한다.
        disconnectIssues(releaseNote);

        Date date = new Date();

        // 배포 상태에 따라 배포 날짜 값을 결정한다.
        if (releaseUpdateRequestDto.getDeployStatus().equals("DEPLOYED")) {
            date = new Date();
        }

        // 수정된 내용을 반영한다.
        releaseNote.updateReleaseNote(
                releaseUpdateRequestDto.getTitle(),
                releaseUpdateRequestDto.getContent(),
                releaseUpdateRequestDto.getSummary(),
                updatedVersion,
                date,
                ReleaseDeployStatus.valueOf(releaseUpdateRequestDto.getDeployStatus())
        );

        return releaseRepository.save(releaseNote);
    }

    /**
     * 릴리즈 노트 수정 및 배포가 가능한지 검사한다.
     */
    private void validateReleaseNoteUpdate(ReleaseNote releaseNote, ReleaseUpdateRequestDto releaseUpdateRequestDto) {
        // 릴리즈 노트가 수정 가능한 상태(PLANNING, DENIED)인지 검사한다.
        if (releaseNote.getDeployStatus().equals(ReleaseDeployStatus.DEPLOYED)) {
            // 만약 이미 DEPLOYED 된 릴리즈 노트인 경우 예외를 발생시킨다.
            throw new CustomException(FAILED_TO_UPDATE_DEPLOYED_RELEASE_VERSION);
        }

        // 만약 요청된 릴리즈 노트의 배포 상태가 DEPLOYED인 경우
        if (releaseUpdateRequestDto.getDeployStatus().equals("DEPLOYED")) {
            Project project = releaseNote.getProject();
            List<ReleaseNote> releaseNotes = releaseRepository.findPreviousReleaseNotes(project, releaseUpdateRequestDto.getVersion());

            // 이전 릴리즈 노트 중 배포되지 않은 것이 있는지 검증하고, 아닌 경우 예외를 발생시킨다.
            releaseNotes
                    .forEach(r -> {
                        if (!r.getDeployStatus().equals(ReleaseDeployStatus.DEPLOYED)) {
                            throw new CustomException(FAILED_TO_UPDATE_RELEASE_DEPLOY_STATUS);
                        }
                    });
        }
    }

    /**
     * 기존의 이슈들에 대해 연결을 해제한다.
     */
    private void disconnectIssues(ReleaseNote releaseNote) {
        releaseNote.getIssues().forEach(Issue::disconnectReleaseNote);
    }

    /**
     * 릴리즈 노트에 이슈를 연결시킨다.
     */
    private void connectIssues(List<Long> issueIds, ReleaseNote savedReleaseNote) {
        List<Issue> issues = getIssuesById(issueIds);

        issues.forEach(i -> {

            // 각각의 이슈들에 이미 연결된 릴리즈 노트가 없는지, 각 이슈들은 완료된 상태인지를 한 번 더 확인한다.

            if (i.getRelease() != null) {
                throw new CustomException(INVALID_ISSUE_WITH_COMPLETED);
            }

            if (i.getLifeCycle() != LifeCycle.DONE) {
                throw new CustomException(INVALID_ISSUE_WITH_NOT_DONE);
            }

            i.updateReleaseNote(savedReleaseNote);
            issueRepository.save(i);
        });
    }

    /**
     * 릴리즈 노트가 생성되었음을 프로젝트 멤버들에게 알리고, 동의를 구하도록 한다.
     */
    private void alertCreatedReleaseNote(Long projectId, Long releaseId) {
        // TODO: 이후 RabbitMQ를 적용시킬 때 구현한다.
    }

    /**
     * 생성된 릴리즈 노트의 동의 여부에 대한 멤버의 목록을 생성한다.
     */
    private void createReleaseApprovals(ReleaseNote releaseNote) {
        // 해당 릴리즈 노트가 들어있는 프로젝트의 멤버 목록을 가져온다.
        List<ProjectMember> members = projectMemberRepository.findByProject(releaseNote.getProject());

        // 릴리즈 노트의 식별 번호와 프로젝트 멤버 식별 번호를 가지고 동의 여부 테이블에 데이터를 생성한다.
        for (ProjectMember member : members) {
            ReleaseApproval releaseApproval = ReleaseApproval.builder()
                    .member(member)
                    .release(releaseNote)
                    .build();

            releaseApprovalRepository.save(releaseApproval);
        }
    }

    /**
     * 릴리즈 노트가 수정되었음을 프로젝트 멤버들에게 알리고, 배포 상태를 알린다.
     */
    private void alertReleaseNoteDeploy(ReleaseNote releaseNote) {
        // TODO: 이후 RabbitMQ를 적용시킬 때 구현한다.

        switch (releaseNote.getDeployStatus().name()) {
            case "PLANNING":
                break;

            case "DENIED":
                break;

            case "DEPLOYED":
                break;

            default:
                break;
        }
    }

    /**
     * 클라이언트로부터 전달받은 버전이 올바른지 검사한다.
     */
    private String updateReleaseVersion(Long releaseId, String version) {
        ReleaseNote releaseNote = getReleaseNoteById(releaseId);

        // 1. 만약 수정하려고 하는 릴리즈 노트의 원래 버전이 1.0.0인 경우 수정하지 못하도록 한다. 이 경우 릴리즈 노트 내용만 수정해야 한다.
        if (!Objects.equals(version, "1.0.0") && Objects.equals(releaseNote.getVersion(), "1.0.0")) {
            throw new CustomException(FAILED_TO_UPDATE_INITIAL_RELEASE_VERSION);
        }

        // 2. 중복된 버전이 있는지 확인한다. 중복된 버전이 존재할 경우 예외를 발생시킨다.
        if (releaseRepository.existsByProjectAndVersion(releaseNote.getProject(), releaseId, version)) {
            throw new CustomException(DUPLICATED_RELEASE_VERSION);
        }

        // 3. 해당 프로젝트의 모든 릴리즈 버전을 가져온 후, 변경하려는 버전을 이어 붙인다.
        List<String> versions = releaseRepository.findByProjectAndNotInVersion(releaseNote.getProject(), releaseNote.getVersion()).stream().map(ReleaseNote::getVersion).collect(Collectors.toList());
        versions.add(version);

        // 4. 변경하려는 버전이 포함된 릴리즈 버전 배열을 오름차순으로 정렬한다.
//        Collections.sort(versions);
        versions = sortVersionByAsc(versions);
        log.info("updateReleaseVersion/versions: {}", versions);

        // 5. 바꾸려는 버전 값이 올바른 버전 값인지를 확인한다.
        validateCorrectVersion(versions);

        return version;
    }

    /**
     * 클라이언트가 수정하고자 하는 버전이 올바른 버전인지 검증한다.
     */
    private void validateCorrectVersion(List<String> versions) {
        int[] majors = versions.stream().mapToInt(v -> Integer.parseInt(v.split("\\.")[0])).toArray();
        int[] minors = versions.stream().mapToInt(v -> Integer.parseInt(v.split("\\.")[1])).toArray();
        int[] patches = versions.stream().mapToInt(v -> Integer.parseInt(v.split("\\.")[2])).toArray();

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
                minorStartIdx = i + 1;

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

        if (end - start == 0) {
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
                validatePatchVersion(patches, minorStartIdx, i);
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

        if (end - start == 0) {
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

    /**
     * 릴리즈 노트 삭제가 가능한지 유효성 검사를 진행한다.
     */
    private void validateReleaseNoteDelete(ReleaseNote releaseNote) {

        // 릴리즈 노트가 삭제 가능한 상태(PLANNING, DENIED)인지 검사한다.
        if (releaseNote.getDeployStatus().equals(ReleaseDeployStatus.DEPLOYED)) {
            // 만약 이미 DEPLOYED 된 릴리즈 노트인 경우 예외를 발생시킨다.
            throw new CustomException(FAILED_TO_DELETE_DEPLOYED_RELEASE_NOTE);
        }

        // 만약 해당 릴리즈 노트 앞에 추가로 생성된 릴리즈 노트가 있을 경우 삭제할 수 없다.
        List<ReleaseNote> releaseNotes = releaseRepository.findNextReleaseNotes(releaseNote.getProject(), releaseNote.getVersion());
        if (releaseNotes.size() > 0) {
            throw new CustomException(FAILED_TO_DELETE_RELEASE_NOTE);
        }
    }

    /**
     * 릴리즈 노트 배포 동의 여부를 선택할 수 있는 건지 확인한다.
     */
    private void validateReleaseNoteApproval(ProjectMember member, ReleaseNote releaseNote) {

        // 만약 릴리즈 노트가 배포된 상태(DEPLOYED)라면 배포 동의를 체크할 수 없다.
        if (releaseNote.getDeployStatus().equals(ReleaseDeployStatus.DEPLOYED)) {
            throw new CustomException(FAILED_TO_APPROVE_RELEASE_NOTE);
        }

        // 만약 릴리즈 노트가 멤버가 속한 프로젝트의 릴리즈 노트가 아닌 경우 예외를 발생시킨다.
        if (!releaseNote.getProject().equals(member.getProject())) {
            throw new CustomException(UNAUTHORIZED_RELEASE_NOTE);
        }
    }

    /**
     * 릴리즈 노트의 배포 동의 여부를 업데이트한다.
     */
    private void updateReleaseNoteApproval(ProjectMember member, ReleaseNote releaseNote, char approval) {
        ReleaseApproval releaseApproval = releaseApprovalRepository.findByMemberAndRelease(member, releaseNote).orElseThrow(() -> new CustomException(NOT_EXISTS_RELEASE_APPROVAL));
        releaseApproval.updateApproval(approval);
        releaseApprovalRepository.save(releaseApproval);
    }

    /**
     * 해당 릴리즈 노트에 대한 프로젝트 멤버들의 업데이트된 배포 동의 여부 목록을 반환한다.
     */
    private List<ReleaseApprovalsResponseDto> getReleaseApprovals(ReleaseNote releaseNote) {
        List<ReleaseApproval> releaseApprovals = releaseApprovalRepository.findAllByRelease(releaseNote);

        if (releaseApprovals == null || releaseApprovals.size() == 0) {
            throw new CustomException(FAILED_TO_GET_RELEASE_APPROVALS);
        }

        return releaseApprovals.stream()
                .map(ReleaseMapper.INSTANCE::toReleaseApprovalsResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * 릴리즈 노트 좌표를 클라이언트에서 새로 받은 값으로 업데이트한다.
     */
    private void updateCoordinates(List<CoordinateDataDto> datas) {
        for (CoordinateDataDto data : datas) {

            // 해당 릴리즈 식별 번호에 대항하는 릴리즈 노트 엔티티를 가져온다.
            ReleaseNote releaseNote = getReleaseNoteById(data.getReleaseId());

            // 해당 릴리즈 노트의 이전 좌표 값과 새로 전달받은 좌표 값이 같은 경우 업데이트를 생략한다.
            Double prevX = releaseNote.getCoordX();
            Double prevY = releaseNote.getCoordY();
            Double newX = data.getCoordX();
            Double newY = data.getCoordY();

            if (!Objects.equals(prevX, newX) && !Objects.equals(prevY, newY)) {
                // x, y 좌표 모두 다른 경우 업데이트 한다.
                releaseNote.updateCoordinates(newX, newY);

            } else if (!Objects.equals(prevX, newX)) {
                // x 좌표가 다른 경우 업데이트한다.
                releaseNote.updateCoordX(newX);

            } else if (!Objects.equals(prevY, newY)) {
                // y 좌표가 다른 경우 업데이트한다.
                releaseNote.updateCoordY(newY);

            } else {
                // 만약 변경된 값이 없는 경우 업데이트를 하지 않고 넘어간다.
                continue;
            }

            releaseRepository.save(releaseNote);
        }
    }

    /**
     * 릴리즈 노트 의견을 저장한다.
     */
    private ReleaseOpinion saveReleaseOpinion(ReleaseNote releaseNote, ProjectMember member, ReleaseOpinionCreateRequestDto releaseOpinionCreateRequestDto) {
        ReleaseOpinion releaseOpinion = ReleaseOpinion.builder()
                .opinion(releaseOpinionCreateRequestDto.getOpinion())
                .release(releaseNote)
                .member(member)
                .build();

        return releaseOpinionRepository.save(releaseOpinion);
    }

    /**
     * 릴리즈 노트 의견 조회 결과를 DTO 리스트로 변환한다.
     */
    private List<ReleaseOpinionsResponseDto> getReleaseOpinionsResponseDto(List<ReleaseOpinion> releaseOpinions) {
        return releaseOpinions.stream()
                .map(ReleaseMapper.INSTANCE::toReleaseOpinionsResponseDto)
                .collect(Collectors.toList());
    }
}
