package com.momentum.releaser.domain.release.dao;

import com.momentum.releaser.domain.project.domain.Project;
import com.momentum.releaser.domain.release.domain.ReleaseNote;

public interface ReleaseRepositoryCustom {

    ReleaseNote findLatestReleaseNote(Project project);
}
