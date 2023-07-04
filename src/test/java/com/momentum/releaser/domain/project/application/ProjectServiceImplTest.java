package com.momentum.releaser.domain.project.application;

import com.momentum.releaser.domain.project.dao.ProjectRepository;
import com.momentum.releaser.domain.project.domain.Project;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ProjectServiceImplTest {


    @Autowired
    EntityManager em;
    @Autowired
    ProjectRepository projectRepository;

    @Test
    public void registerProject() {
        Project project = new Project("project", "momentum", "", 'Y');
        projectRepository.save(project);
    }

}