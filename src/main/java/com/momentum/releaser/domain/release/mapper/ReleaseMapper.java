package com.momentum.releaser.domain.release.mapper;

import com.momentum.releaser.domain.release.domain.ReleaseNote;
import com.momentum.releaser.domain.release.dto.ReleaseDataDto.ReleasesDataDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ReleaseMapper {

    ReleaseMapper INSTANCE = Mappers.getMapper(ReleaseMapper.class);

    /**
     * Entity (ReleaseNote) -> DTO (ReleasesDataDto)
     */
    ReleasesDataDto toReleasesDataDto(ReleaseNote releaseNote);
}
