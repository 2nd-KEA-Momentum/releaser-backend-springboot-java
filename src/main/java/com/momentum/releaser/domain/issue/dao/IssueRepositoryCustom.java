package com.momentum.releaser.domain.issue.dao;

import com.momentum.releaser.domain.issue.domain.Issue;
import com.momentum.releaser.domain.issue.domain.IssueNum;
import com.momentum.releaser.domain.issue.dto.IssueResDto;
import com.momentum.releaser.domain.issue.dto.IssueResDto.IssueInfoRes;
import com.momentum.releaser.domain.project.domain.Project;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface IssueRepositoryCustom {

    List<IssueInfoRes> getIssues(Project project);

    Long getIssueNum(Project project);

    void deleteByIssueNum();
}
