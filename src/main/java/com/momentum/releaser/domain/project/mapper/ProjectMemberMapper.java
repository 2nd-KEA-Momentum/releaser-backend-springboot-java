package com.momentum.releaser.domain.project.mapper;

import com.momentum.releaser.domain.project.domain.ProjectMember;
import com.momentum.releaser.domain.project.dto.ProjectMemberDatoDto.ProjectMembersDataDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ProjectMemberMapper {

    @Mapping(target = "name", source = "projectMember.user.name")
    @Mapping(target = "profileImg", source = "projectMember.user.img")
    ProjectMembersDataDto toProjectMembersDataDto(ProjectMember projectMember);
}
