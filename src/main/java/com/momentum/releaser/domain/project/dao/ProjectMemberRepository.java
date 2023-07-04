package com.momentum.releaser.domain.project.dao;

import com.momentum.releaser.domain.project.domain.Project;
import com.momentum.releaser.domain.project.domain.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel="project-member", path="project-member")
public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {
    ProjectMember findByProject(Project updateProject);
}
