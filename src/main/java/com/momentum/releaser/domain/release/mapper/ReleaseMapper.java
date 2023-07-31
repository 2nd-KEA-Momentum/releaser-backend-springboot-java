package com.momentum.releaser.domain.release.mapper;

import com.momentum.releaser.domain.issue.mapper.IssueMapper;
import com.momentum.releaser.domain.project.mapper.ProjectMemberMapper;
import com.momentum.releaser.domain.release.domain.ReleaseApproval;
import com.momentum.releaser.domain.release.domain.ReleaseNote;
import com.momentum.releaser.domain.release.domain.ReleaseOpinion;
import com.momentum.releaser.domain.release.dto.ReleaseDataDto.ReleaseApprovalsDataDTO;
import com.momentum.releaser.domain.release.dto.ReleaseDataDto.ReleaseOpinionsDataDTO;
import com.momentum.releaser.domain.release.dto.ReleaseDataDto.ReleasesDataDTO;
import com.momentum.releaser.domain.release.dto.ReleaseResponseDto.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(uses = {ReleaseMapper.class, IssueMapper.class, ProjectMemberMapper.class})
public interface ReleaseMapper {

    ReleaseMapper INSTANCE = Mappers.getMapper(ReleaseMapper.class);

    /**
     * Entity (ReleaseNote) -> DTO (ReleasesDataDto)
     */
    ReleasesDataDTO toReleasesDataDto(ReleaseNote releaseNote);

    /**
     * Entity (ReleaseNote) -> DTO(ReleaseCreateAndUpdateResponseDto)
     */
    ReleaseCreateAndUpdateResponseDTO toReleaseCreateAndUpdateResponseDto(ReleaseNote releaseNote);

    /**
     * Entity (ReleaseNote) -> DTO(ReleaseInfoResponseDto)
     */
    @Mapping(target = "opinions", source = "releaseOpinionsDataDtos")
    ReleaseInfoResponseDTO toReleaseInfoResponseDto(ReleaseNote releaseNote, List<ReleaseOpinionsDataDTO> releaseOpinionsDataDtos);

    /**
     * Entity(ReleaseApproval) -> DTO(ReleaseApprovalsDataDto)
     */
    @Mapping(target = "memberId", source = "releaseApproval.member.memberId")
    @Mapping(target = "memberName", source = "releaseApproval.member.user.name")
    @Mapping(target = "memberProfileImg", source = "releaseApproval.member.user.img")
    @Mapping(target = "position", source = "releaseApproval.member.position")
    ReleaseApprovalsDataDTO toReleaseApprovalsDataDto(ReleaseApproval releaseApproval);

    /**
     * Entity(ReleaseOpinion) -> DTO(ReleaseOpinionsDataDto)
     */
//    @Mapping(target = "memberId", source = "releaseOpinion.member.memberId")
//    @Mapping(target = "memberName", source = "releaseOpinion.member.user.name")
//    @Mapping(target = "memberProfileImg", source = "releaseOpinion.member.user.img")
//    ReleaseOpinionsDataDto toReleaseOpinionsDataDto(ReleaseOpinion releaseOpinion);

    /**
     * Entity(ReleaseApproval) -> DTO(ReleaseApprovalsResponseDto)
     */
    @Mapping(target = "memberId", source = "releaseApproval.member.memberId")
    @Mapping(target = "memberName", source = "releaseApproval.member.user.name")
    @Mapping(target = "memberProfileImg", source = "releaseApproval.member.user.img")
    @Mapping(target = "position", source = "releaseApproval.member.position")
    ReleaseApprovalsResponseDTO toReleaseApprovalsResponseDto(ReleaseApproval releaseApproval);

    /**
     * Entity(ReleaseOpinion) -> DTO(ReleaseOpinionCreateResponseDto)
     */
    ReleaseOpinionCreateResponseDTO toReleaseOpinionCreateResponseDto(ReleaseOpinion releaseOpinion);

    /**
     * Entity(ReleaseOpinion) -> DTO(ReleaseOpinionsDataDto)
     */
    @Mapping(target = "memberId", source = "releaseOpinion.member.memberId")
    @Mapping(target = "memberName", source = "releaseOpinion.member.user.name")
    @Mapping(target = "memberProfileImg", source = "releaseOpinion.member.user.img")
    ReleaseOpinionsResponseDTO toReleaseOpinionsResponseDto(ReleaseOpinion releaseOpinion);

}
