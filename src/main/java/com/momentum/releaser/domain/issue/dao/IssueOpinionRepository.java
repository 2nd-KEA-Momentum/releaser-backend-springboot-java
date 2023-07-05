package com.momentum.releaser.domain.issue.dao;

import com.momentum.releaser.domain.issue.domain.IssueOpinion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel="issue-opinion", path="issue-opinion")
public interface IssueOpinionRepository extends JpaRepository<IssueOpinion, Long> {

}
