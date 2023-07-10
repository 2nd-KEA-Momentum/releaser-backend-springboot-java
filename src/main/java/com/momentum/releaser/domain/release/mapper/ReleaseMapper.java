package com.momentum.releaser.domain.release.mapper;

import com.momentum.releaser.domain.issue.mapper.IssueMapper;
import com.momentum.releaser.domain.project.mapper.ProjectMemberMapper;
import com.momentum.releaser.domain.release.domain.ReleaseApproval;
import com.momentum.releaser.domain.release.domain.ReleaseNote;
import com.momentum.releaser.domain.release.domain.ReleaseOpinion;
import com.momentum.releaser.domain.release.dto.ReleaseDataDto.ReleaseApprovalsDataDto;
import com.momentum.releaser.domain.release.dto.ReleaseDataDto.ReleaseOpinionsDataDto;
import com.momentum.releaser.domain.release.dto.ReleaseDataDto.ReleasesDataDto;
import com.momentum.releaser.domain.release.dto.ReleaseResponseDto.ReleaseApprovalsResponseDto;
import com.momentum.releaser.domain.release.dto.ReleaseResponseDto.ReleaseCreateResponseDto;
import com.momentum.releaser.domain.release.dto.ReleaseResponseDto.ReleaseInfoResponseDto;
import com.momentum.releaser.domain.release.dto.ReleaseResponseDto.ReleaseOpinionCreateResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {IssueMapper.class, ProjectMemberMapper.class})
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
    @Mapping(target = "opinions", source = "releaseNote.releaseOpinions")
    ReleaseInfoResponseDto toReleaseInfoResponseDto(ReleaseNote releaseNote);

    /**
     * Entity(ReleaseApproval) -> DTO(ReleaseApprovalsDataDto)
     */
    @Mapping(target = "memberId", source = "releaseApproval.member.memberId")
    @Mapping(target = "memberName", source = "releaseApproval.member.user.name")
    @Mapping(target = "memberProfileImg", source = "releaseApproval.member.user.img")
    ReleaseApprovalsDataDto toReleaseApprovalsDataDto(ReleaseApproval releaseApproval);

    /**
     * Entity(ReleaseOpinion) -> DTO(ReleaseOpinionsDataDto)
     */
    @Mapping(target = "memberId", source = "releaseOpinion.member.memberId")
    @Mapping(target = "memberName", source = "releaseOpinion.member.user.name")
    @Mapping(target = "memberProfileImg", source = "releaseOpinion.member.user.img")
    ReleaseOpinionsDataDto toReleaseOpinionsDataDto(ReleaseOpinion releaseOpinion);

    /**
     * Entity(ReleaseApproval) -> DTO(ReleaseApprovalsResponseDto)
     */
    @Mapping(target = "memberId", source = "releaseApproval.member.memberId")
    @Mapping(target = "memberName", source = "releaseApproval.member.user.name")
    @Mapping(target = "memberProfileImg", source = "releaseApproval.member.user.img")
    ReleaseApprovalsResponseDto toReleaseApprovalsResponseDto(ReleaseApproval releaseApproval);

    /**
     * Entity(ReleaseOpinion) -> DTO(ReleaseOpinionCreateResponseDto)
     */
    ReleaseOpinionCreateResponseDto toReleaseOpinionCreateResponseDto(ReleaseOpinion releaseOpinion);
}
