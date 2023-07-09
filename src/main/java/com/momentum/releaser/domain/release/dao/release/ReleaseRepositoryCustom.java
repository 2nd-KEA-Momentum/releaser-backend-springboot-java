package com.momentum.releaser.domain.release.dao.release;

import com.momentum.releaser.domain.project.domain.Project;
import com.momentum.releaser.domain.release.domain.ReleaseNote;

import java.util.List;
import java.util.Optional;

/**
 * Querydsl을 사용하는 repository
 * @see ReleaseRepositoryImpl
 */
public interface ReleaseRepositoryCustom {

    boolean existsByProjectAndVersion(Project project, Long releaseId, String version);

    Optional<ReleaseNote> findLatestVersionByProject(Project project);

    List<ReleaseNote> findByProjectAndNotInVersion(Project project, String version);

    List<ReleaseNote> findPreviousReleaseNotes(Project project, String version);

    List<ReleaseNote> findNextReleaseNotes(Project project, String version);
}
