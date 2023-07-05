package com.momentum.releaser.domain.release.mapper;

import com.momentum.releaser.domain.release.domain.ReleaseOpinion;
import com.momentum.releaser.domain.release.dto.ReleaseDataDto.ReleaseOpinionsDataDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ReleaseOpinionMapper {

    /**
     * Entity(ReleaseOpinion) -> DTO(ReleaseOpinionsDataDto)
     */
    @Mapping(target = "opinionId", source = "releaseOpinion.releaseOpinionId")
    @Mapping(target = "memberId", source = "releaseOpinion.member.memberId")
    @Mapping(target = "memberName", source = "releaseOpinion.member.user.name")
    @Mapping(target = "memberProfileImg", source = "releaseOpinion.member.user.img")
    ReleaseOpinionsDataDto toReleaseOpinionsDataDto(ReleaseOpinion releaseOpinion);
}
