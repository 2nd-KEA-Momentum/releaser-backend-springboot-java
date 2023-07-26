package com.momentum.releaser.domain.project.dao;

import com.momentum.releaser.domain.project.domain.Project;
import com.momentum.releaser.domain.project.dto.ProjectResponseDto.GetMembersRes;

import java.util.List;

public interface ProjectRepositoryCustom {

    List<GetMembersRes> getMemberList(Project project);

}
