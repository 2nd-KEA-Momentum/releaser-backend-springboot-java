package com.momentum.releaser.domain.release.dao;

import com.momentum.releaser.domain.release.domain.ReleaseNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel="release-note", path="release-note")
public interface ReleaseRepository extends JpaRepository<ReleaseNote, Long> {
}
