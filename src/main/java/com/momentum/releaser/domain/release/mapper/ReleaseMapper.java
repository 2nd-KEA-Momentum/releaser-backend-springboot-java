package com.momentum.releaser.domain.release.mapper;

import com.momentum.releaser.domain.issue.mapper.IssueMapper;
import com.momentum.releaser.domain.project.mapper.ProjectMemberMapper;
import com.momentum.releaser.domain.release.domain.ReleaseNote;
import com.momentum.releaser.domain.release.dto.ReleaseDataDto.ReleasesDataDto;
import com.momentum.releaser.domain.release.dto.ReleaseResponseDto.ReleaseCreateResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {IssueMapper.class, ReleaseOpinionMapper.class, ProjectMemberMapper.class})
public interface ReleaseMapper {

    ReleaseMapper INSTANCE = Mappers.getMapper(ReleaseMapper.class);

    /**
     * Entity (ReleaseNote) -> DTO (ReleasesDataDto)
     */
    ReleasesDataDto toReleasesDataDto(ReleaseNote releaseNote);

    /**
     * Entity (ReleaseNote) -> DTO(ReleaseCreateResponseDto)
     */
    ReleaseCreateResponseDto toReleaseCreateResponseDto(ReleaseNote releaseNote);

    /**
     * Entity (ReleaseNote) -> DTO(ReleaseInfoResponseDto)
     */
//    ReleaseInfoResponseDto toReleaseInfoResponseDto(ReleaseNote releaseNote);
}
