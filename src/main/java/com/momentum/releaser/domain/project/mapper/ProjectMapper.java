package com.momentum.releaser.domain.project.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.momentum.releaser.domain.project.domain.Project;
import com.momentum.releaser.domain.project.domain.ProjectMember;
import com.momentum.releaser.domain.project.dto.ProjectResponseDto.ProjectInfoResponseDTO;
import com.momentum.releaser.domain.release.dto.ReleaseResponseDto.ReleasesResponseDto;
import com.momentum.releaser.domain.release.mapper.ReleaseMapper;


@Mapper(uses = ReleaseMapper.class)
public interface ProjectMapper {

    ProjectMapper INSTANCE = Mappers.getMapper(ProjectMapper.class);

    /**
     * Entity(Project) -> DTO(ProjectInfoRes)
     */
    ProjectInfoResponseDTO toProjectInfoRes(Project project);

    /**
     * Entity(Project) -> DTO(ReleasesResponseDto)
     */
    ReleasesResponseDto toReleasesResponseDto(Project project, ProjectMember member);
}
