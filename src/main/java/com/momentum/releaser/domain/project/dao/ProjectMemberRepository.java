package com.momentum.releaser.domain.project.dao;

import com.momentum.releaser.domain.project.domain.Project;
import com.momentum.releaser.domain.project.domain.ProjectMember;
import com.momentum.releaser.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@RepositoryRestResource(collectionResourceRel="project-member", path="project-member")
public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {
    List<ProjectMember> findByProject(Project updateProject);

    List<ProjectMember> findByUser(User user);

    ProjectMember findByUserAndProject(User user, Project project);
}
