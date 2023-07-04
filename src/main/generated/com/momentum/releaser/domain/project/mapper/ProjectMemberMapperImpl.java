package com.momentum.releaser.domain.project.mapper;

import com.momentum.releaser.domain.project.domain.ProjectMember;
import com.momentum.releaser.domain.project.dto.ProjectMemberDatoDto.ProjectMembersDataDto;
import com.momentum.releaser.domain.project.dto.ProjectMemberDatoDto.ProjectMembersDataDto.ProjectMembersDataDtoBuilder;
import com.momentum.releaser.domain.user.domain.User;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-07-05T08:49:37+0900",
    comments = "version: 1.4.2.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.1.1.jar, environment: Java 19.0.2 (Oracle Corporation)"
)
public class ProjectMemberMapperImpl implements ProjectMemberMapper {

    @Override
    public ProjectMembersDataDto toProjectMembersDataDto(ProjectMember projectMember) {
        if ( projectMember == null ) {
            return null;
        }

        ProjectMembersDataDtoBuilder projectMembersDataDto = ProjectMembersDataDto.builder();

        projectMembersDataDto.name( projectMemberUserName( projectMember ) );
        projectMembersDataDto.profileImg( projectMemberUserImg( projectMember ) );
        projectMembersDataDto.memberId( projectMember.getMemberId() );

        return projectMembersDataDto.build();
    }

    private String projectMemberUserName(ProjectMember projectMember) {
        if ( projectMember == null ) {
            return null;
        }
        User user = projectMember.getUser();
        if ( user == null ) {
            return null;
        }
        String name = user.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }

    private String projectMemberUserImg(ProjectMember projectMember) {
        if ( projectMember == null ) {
            return null;
        }
        User user = projectMember.getUser();
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
