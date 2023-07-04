package com.momentum.releaser.domain.issue.mapper;

import com.momentum.releaser.domain.issue.domain.Issue;
import com.momentum.releaser.domain.issue.dto.IssueDataDto.ConnectedIssuesDataDto;
import com.momentum.releaser.domain.issue.dto.IssueDataDto.ConnectedIssuesDataDto.ConnectedIssuesDataDtoBuilder;
import com.momentum.releaser.domain.project.domain.ProjectMember;
import com.momentum.releaser.domain.user.domain.User;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-07-05T08:49:37+0900",
    comments = "version: 1.4.2.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.1.1.jar, environment: Java 19.0.2 (Oracle Corporation)"
)
public class IssueMapperImpl implements IssueMapper {

    @Override
    public ConnectedIssuesDataDto toConnectedIssuesDataDto(Issue issue) {
        if ( issue == null ) {
            return null;
        }

        ConnectedIssuesDataDtoBuilder connectedIssuesDataDto = ConnectedIssuesDataDto.builder();

        connectedIssuesDataDto.memberId( issueMemberMemberId( issue ) );
        connectedIssuesDataDto.memberName( issueMemberUserName( issue ) );
        connectedIssuesDataDto.memberProfileImg( issueMemberUserImg( issue ) );
        connectedIssuesDataDto.issueId( issue.getIssueId() );
        connectedIssuesDataDto.title( issue.getTitle() );
        connectedIssuesDataDto.lifeCycle( issue.getLifeCycle() );
        connectedIssuesDataDto.endDate( issue.getEndDate() );

        return connectedIssuesDataDto.build();
    }

    private Long issueMemberMemberId(Issue issue) {
        if ( issue == null ) {
            return null;
        }
        ProjectMember member = issue.getMember();
        if ( member == null ) {
            return null;
        }
        Long memberId = member.getMemberId();
        if ( memberId == null ) {
            return null;
        }
        return memberId;
    }

    private String issueMemberUserName(Issue issue) {
        if ( issue == null ) {
            return null;
        }
        ProjectMember member = issue.getMember();
        if ( member == null ) {
            return null;
        }
        User user = member.getUser();
        if ( user == null ) {
            return null;
        }
        String name = user.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }

    private String issueMemberUserImg(Issue issue) {
        if ( issue == null ) {
            return null;
        }
        ProjectMember member = issue.getMember();
        if ( member == null ) {
            return null;
        }
        User user = member.getUser();
        if ( user == null ) {
            return null;
        }
        String img = user.getImg();
        if ( img == null ) {
            return null;
        }
        return img;
    }
}
