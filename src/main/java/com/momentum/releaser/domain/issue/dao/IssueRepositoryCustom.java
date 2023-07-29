package com.momentum.releaser.domain.issue.dao;

import com.momentum.releaser.domain.issue.domain.Issue;
import com.momentum.releaser.domain.issue.dto.IssueResponseDto.*;
import com.momentum.releaser.domain.project.domain.Project;
import com.momentum.releaser.domain.release.domain.ReleaseNote;

import java.util.List;

public interface IssueRepositoryCustom {

    List<IssueInfoResponseDTO> getIssues(Project project);

    Long getIssueNum(Project project);

    void deleteByIssueNum();

    List<DoneIssuesResponseDTO> getDoneIssues(Project findProject, String status);

    List<ConnectionIssuesResponseDTO> getConnectionIssues(Project findProject, ReleaseNote findReleaseNote);

    List<OpinionInfoResponseDTO> getIssueOpinion(Issue issue);


}
