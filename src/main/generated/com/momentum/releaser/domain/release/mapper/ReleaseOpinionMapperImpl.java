package com.momentum.releaser.domain.release.mapper;

import com.momentum.releaser.domain.project.domain.ProjectMember;
import com.momentum.releaser.domain.release.domain.ReleaseOpinion;
import com.momentum.releaser.domain.release.dto.ReleaseDataDto.ReleaseOpinionsDataDto;
import com.momentum.releaser.domain.release.dto.ReleaseDataDto.ReleaseOpinionsDataDto.ReleaseOpinionsDataDtoBuilder;
import com.momentum.releaser.domain.user.domain.User;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-07-05T08:49:37+0900",
    comments = "version: 1.4.2.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.1.1.jar, environment: Java 19.0.2 (Oracle Corporation)"
)
public class ReleaseOpinionMapperImpl implements ReleaseOpinionMapper {

    @Override
    public ReleaseOpinionsDataDto toReleaseOpinionsDataDto(ReleaseOpinion releaseOpinion) {
        if ( releaseOpinion == null ) {
            return null;
        }

        ReleaseOpinionsDataDtoBuilder releaseOpinionsDataDto = ReleaseOpinionsDataDto.builder();

        releaseOpinionsDataDto.opinionId( releaseOpinion.getReleaseOpinionId() );
        releaseOpinionsDataDto.memberId( releaseOpinionMemberMemberId( releaseOpinion ) );
        releaseOpinionsDataDto.memberName( releaseOpinionMemberUserName( releaseOpinion ) );
        releaseOpinionsDataDto.memberProfileImg( releaseOpinionMemberUserImg( releaseOpinion ) );
        releaseOpinionsDataDto.opinion( releaseOpinion.getOpinion() );

        return releaseOpinionsDataDto.build();
    }

    private Long releaseOpinionMemberMemberId(ReleaseOpinion releaseOpinion) {
        if ( releaseOpinion == null ) {
            return null;
        }
        ProjectMember member = releaseOpinion.getMember();
        if ( member == null ) {
            return null;
        }
        Long memberId = member.getMemberId();
        if ( memberId == null ) {
            return null;
        }
        return memberId;
    }

    private String releaseOpinionMemberUserName(ReleaseOpinion releaseOpinion) {
        if ( releaseOpinion == null ) {
            return null;
        }
        ProjectMember member = releaseOpinion.getMember();
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

    private String releaseOpinionMemberUserImg(ReleaseOpinion releaseOpinion) {
        if ( releaseOpinion == null ) {
            return null;
        }
        ProjectMember member = releaseOpinion.getMember();
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
