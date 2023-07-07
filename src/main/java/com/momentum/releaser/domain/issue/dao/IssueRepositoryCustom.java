package com.momentum.releaser.domain.issue.dao;

import com.momentum.releaser.domain.issue.dto.IssueResDto;
import com.momentum.releaser.domain.issue.dto.IssueResDto.IssueInfoRes;

import java.util.List;

public interface IssueRepositoryCustom {

    List<IssueInfoRes> getIssues();
}
