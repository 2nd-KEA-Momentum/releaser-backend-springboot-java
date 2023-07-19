package com.momentum.releaser.domain.issue.mapper;

import com.momentum.releaser.domain.issue.domain.Issue;
import com.momentum.releaser.domain.issue.dto.IssueDataDto.ConnectedIssuesDataDto;
import com.momentum.releaser.domain.issue.dto.IssueResDto;
import com.momentum.releaser.domain.issue.dto.IssueResDto.GetIssue;
import com.momentum.releaser.domain.issue.dto.IssueResDto.OpinionInfoRes;
import com.momentum.releaser.domain.project.dto.ProjectResDto;
import com.momentum.releaser.domain.project.dto.ProjectResDto.GetMembersRes;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface IssueMapper {

    IssueMapper INSTANCE = Mappers.getMapper(IssueMapper.class);


    /**
     * Entity (Issue) -> DTO(ConnectedIssuesDataDto)
     */
    @Mapping(target = "memberId", source = "issue.member.memberId")
    @Mapping(target = "memberName", source = "issue.member.user.name")
    @Mapping(target = "memberProfileImg", source = "issue.member.user.img")
    ConnectedIssuesDataDto toConnectedIssuesDataDto(Issue issue);

    @Mapping(source = "issue.issueNum.issueNum", target = "issueNum")
    @Mapping(source = "issue.member.memberId", target = "manager")
    @Mapping(target = "deployYN", expression = "java(issue.getRelease() != null && issue.getRelease().getDeployStatus().equals(ReleaseStatus.DEPLOYED) ? 'Y' : 'N')")
    GetIssue mapToGetIssue(Issue issue, List<GetMembersRes> memberRes, List<OpinionInfoRes> opinionRes);
}
