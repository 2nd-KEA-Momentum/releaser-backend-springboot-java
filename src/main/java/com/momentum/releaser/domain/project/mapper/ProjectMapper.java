package com.momentum.releaser.domain.project.mapper;

import com.momentum.releaser.domain.project.domain.Project;
import com.momentum.releaser.domain.project.domain.ProjectMember;
import com.momentum.releaser.domain.project.dto.ProjectResDto;
import com.momentum.releaser.domain.project.dto.ProjectResDto.GetProjectRes;
import com.momentum.releaser.domain.project.dto.ProjectResDto.ProjectInfoRes;
import com.momentum.releaser.domain.release.dto.ReleaseResponseDto.ReleasesResponseDto;
import com.momentum.releaser.domain.release.mapper.ReleaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = ReleaseMapper.class)
public interface ProjectMapper {

    ProjectMapper INSTANCE = Mappers.getMapper(ProjectMapper.class);

    /**
     * Entity(Project) -> DTO(ProjectInfoRes)
     */
    @Mapping(target = "projectName", source ="project.title")
    ProjectInfoRes toProjectInfoRes(Project project);

    /**
     * Entity(Project) -> DTO(ReleasesResponseDto)
     */
    ReleasesResponseDto toReleasesResponseDto(Project project, ProjectMember member);
}
