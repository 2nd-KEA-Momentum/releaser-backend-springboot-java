package com.momentum.releaser.domain.project.mapper;

import com.momentum.releaser.domain.project.domain.ProjectMember;
import com.momentum.releaser.domain.project.dto.ProjectMemberDatoDto.ProjectMembersDataDto;
import com.momentum.releaser.domain.project.dto.ProjectResDto;
import com.momentum.releaser.domain.project.dto.ProjectResDto.GetMembersRes;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProjectMemberMapper {
    ProjectMemberMapper INSTANCE = Mappers.getMapper(ProjectMemberMapper.class);


    @Mapping(target = "name", source = "projectMember.user.name")
    @Mapping(target = "profileImg", source = "projectMember.user.img")
    ProjectMembersDataDto toProjectMembersDataDto(ProjectMember projectMember);


    @Mapping(source = "projectMember.position", target = "position")
    @Mapping(target = "userId", source = "projectMember.user.userId")
    @Mapping(target = "name", source = "projectMember.user.name")
    @Mapping(target = "img", source = "projectMember.user.img")
    GetMembersRes toGetMembersRes(ProjectMember projectMember);
}
