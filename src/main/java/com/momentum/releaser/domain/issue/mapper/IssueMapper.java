package com.momentum.releaser.domain.issue.mapper;

import java.util.List;

import com.momentum.releaser.domain.issue.dto.IssueResponseDto;
import com.momentum.releaser.domain.issue.dto.IssueResponseDto.IssueModifyResponseDTO;
import com.momentum.releaser.domain.project.domain.ProjectMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.momentum.releaser.domain.issue.domain.Issue;
import com.momentum.releaser.domain.issue.dto.IssueDataDto.ConnectedIssuesDataDTO;
import com.momentum.releaser.domain.issue.dto.IssueResponseDto.IssueDetailsDTO;
import com.momentum.releaser.domain.issue.dto.IssueResponseDto.OpinionInfoResponseDTO;
import com.momentum.releaser.domain.project.dto.ProjectDataDto.GetMembersDataDTO;
import com.momentum.releaser.domain.project.mapper.ProjectMapper;

@Mapper(uses = { ProjectMapper.class })
public interface IssueMapper {

    IssueMapper INSTANCE = Mappers.getMapper(IssueMapper.class);

    /**
     * Entity (Issue) -> DTO(ConnectedIssuesDataDto)
     */
    @Mapping(target = "memberId", source = "issue.member.memberId")
    @Mapping(target = "memberName", source = "issue.member.user.name")
    @Mapping(target = "memberProfileImg", source = "issue.member.user.img")
    ConnectedIssuesDataDTO toConnectedIssuesDataDto(Issue issue);

    /**
     * Entity (Issue), DTO(GetMembers, OpinionInfoResponseDTO) -> DTO(ConnectedIssuesDataDto)
     */
    @Mapping(source = "issue.issueNum.issueNum", target = "issueNum")
    @Mapping(source = "issue.member.memberId", target = "manager")
    @Mapping(target = "memberList", source = "memberRes")
    @Mapping(target = "opinionList", source = "opinionRes")
    IssueDetailsDTO mapToGetIssue(Issue issue, List<GetMembersDataDTO> memberRes, List<OpinionInfoResponseDTO> opinionRes);

    /**
     * Entity (ProjectMember) -> DTO(IssueModifyResponseDTO)
     */
    IssueModifyResponseDTO toIssueModifyResponseDTO(ProjectMember projectMember);
}
