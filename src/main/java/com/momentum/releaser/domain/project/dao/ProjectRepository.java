package com.momentum.releaser.domain.project.dao;

import com.momentum.releaser.domain.project.domain.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource(collectionResourceRel="project", path="project")
public interface ProjectRepository extends JpaRepository<Project, Long> , ProjectRepositoryCustom{
    Optional<Project> findByLink(String link);
}
