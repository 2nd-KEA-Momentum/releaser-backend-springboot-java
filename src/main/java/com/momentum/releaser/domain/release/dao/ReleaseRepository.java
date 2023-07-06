package com.momentum.releaser.domain.release.dao;

import com.momentum.releaser.domain.project.domain.Project;
import com.momentum.releaser.domain.release.domain.ReleaseNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel="release-note", path="release-note")
public interface ReleaseRepository extends JpaRepository<ReleaseNote, Long>, ReleaseRepositoryCustom {

    List<ReleaseNote> findAllByProject(Project project);

    boolean existsByVersion(String version);

}
