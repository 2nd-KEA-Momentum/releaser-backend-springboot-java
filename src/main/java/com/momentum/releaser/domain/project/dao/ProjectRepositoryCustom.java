package com.momentum.releaser.domain.project.dao;

import java.util.List;

import com.momentum.releaser.domain.project.domain.Project;
import com.momentum.releaser.domain.project.dto.ProjectDataDto.GetMembers;

public interface ProjectRepositoryCustom {

    // 프로젝트에 속한 멤버들의 정보를 List 형태로 반환
    List<GetMembers> getMemberList(Project project);

}
