package com.momentum.releaser.domain.project.mapper;

import com.momentum.releaser.domain.project.domain.ProjectMember;
import com.momentum.releaser.domain.project.dto.ProjectMemberDataDto.ProjectMembersDataDTO;
import com.momentum.releaser.domain.project.dto.ProjectMemberResponseDto.MembersResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProjectMemberMapper {

    ProjectMemberMapper INSTANCE = Mappers.getMapper(ProjectMemberMapper.class);

    @Mapping(target = "name", source = "projectMember.user.name")
    @Mapping(target = "profileImg", source = "projectMember.user.img")
    ProjectMembersDataDTO toProjectMembersDataDto(ProjectMember projectMember);

    @Mapping(target = "link", source = "projectMember.project.link")
    @Mapping(target = "position", source = "projectMember.position")
    @Mapping(target = "userId", source = "projectMember.user.userId")
    @Mapping(target = "name", source = "projectMember.user.name")
    @Mapping(target = "img", source = "projectMember.user.img")
    MembersResponseDTO toGetMembersRes(ProjectMember projectMember);
}
