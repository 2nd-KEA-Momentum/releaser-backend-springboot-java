package com.momentum.releaser.domain.issue.mapper;

import com.momentum.releaser.domain.issue.domain.Issue;
import com.momentum.releaser.domain.issue.dto.IssueDataDto.ConnectedIssuesDataDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface IssueMapper {

    /**
     * Entity (Issue) -> DTO(ConnectedIssuesDataDto)
     */
    @Mapping(target = "memberId", source = "issue.member.memberId")
    @Mapping(target = "memberName", source = "issue.member.user.name")
    @Mapping(target = "memberProfileImg", source = "issue.member.user.img")
    ConnectedIssuesDataDto toConnectedIssuesDataDto(Issue issue);
}
