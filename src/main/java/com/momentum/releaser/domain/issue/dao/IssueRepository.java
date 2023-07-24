package com.momentum.releaser.domain.issue.dao;

import com.momentum.releaser.domain.issue.domain.Issue;
import com.momentum.releaser.domain.release.domain.ReleaseNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel="issue", path="issue")
public interface IssueRepository extends JpaRepository<Issue, Long>, IssueRepositoryCustom, QuerydslPredicateExecutor<Issue> {
    List<Issue> findByRelease(ReleaseNote note);

}
