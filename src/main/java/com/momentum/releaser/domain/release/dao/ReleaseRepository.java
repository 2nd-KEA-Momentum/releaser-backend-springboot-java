package com.momentum.releaser.domain.release.dao;

import com.momentum.releaser.domain.release.domain.Release;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel="release", path="release")
public interface ReleaseRepository extends JpaRepository<Release, Long> {
}
