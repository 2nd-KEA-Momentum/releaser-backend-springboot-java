package com.momentum.releaser.domain.issue.dao;

import com.momentum.releaser.domain.issue.domain.Issue;
import com.momentum.releaser.domain.issue.dto.IssueResponseDto.*;
import com.momentum.releaser.domain.project.domain.Project;
import com.momentum.releaser.domain.release.domain.ReleaseNote;

import java.util.List;

public interface IssueRepositoryCustom {

    List<IssueInfoRes> getIssues(Project project);

    Long getIssueNum(Project project);

    void deleteByIssueNum();

    List<GetDoneIssues> getDoneIssues(Project findProject, String status);

    List<GetConnectionIssues> getConnectionIssues(Project findProject, ReleaseNote findReleaseNote);


    List<OpinionInfoRes> getIssueOpinion(Issue issue);


}
