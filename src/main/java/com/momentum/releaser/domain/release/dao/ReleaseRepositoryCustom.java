package com.momentum.releaser.domain.release.dao;

import com.momentum.releaser.domain.project.domain.Project;
import com.momentum.releaser.domain.release.domain.ReleaseNote;

import java.util.List;
import java.util.Optional;

public interface ReleaseRepositoryCustom {

    Optional<ReleaseNote> findTopByProject(Project project);

    List<ReleaseNote> findByProjectAndNotInVersion(Project project, String version);
}
