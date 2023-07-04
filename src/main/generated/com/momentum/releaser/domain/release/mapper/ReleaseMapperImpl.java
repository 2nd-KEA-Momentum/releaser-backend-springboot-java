package com.momentum.releaser.domain.release.mapper;

import com.momentum.releaser.domain.issue.domain.Issue;
import com.momentum.releaser.domain.issue.dto.IssueDataDto.ConnectedIssuesDataDto;
import com.momentum.releaser.domain.issue.mapper.IssueMapper;
import com.momentum.releaser.domain.release.domain.ReleaseNote;
import com.momentum.releaser.domain.release.domain.ReleaseOpinion;
import com.momentum.releaser.domain.release.dto.ReleaseDataDto.ReleaseOpinionsDataDto;
import com.momentum.releaser.domain.release.dto.ReleaseDataDto.ReleasesDataDto;
import com.momentum.releaser.domain.release.dto.ReleaseDataDto.ReleasesDataDto.ReleasesDataDtoBuilder;
import com.momentum.releaser.domain.release.dto.ReleaseResponseDto.ReleaseInfoResponseDto;
import com.momentum.releaser.domain.release.dto.ReleaseResponseDto.ReleaseInfoResponseDto.ReleaseInfoResponseDtoBuilder;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.mapstruct.factory.Mappers;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-07-05T08:49:37+0900",
    comments = "version: 1.4.2.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.1.1.jar, environment: Java 19.0.2 (Oracle Corporation)"
)
public class ReleaseMapperImpl implements ReleaseMapper {

    private final IssueMapper issueMapper = Mappers.getMapper( IssueMapper.class );
    private final ReleaseOpinionMapper releaseOpinionMapper = Mappers.getMapper( ReleaseOpinionMapper.class );

    @Override
    public ReleasesDataDto toReleasesDataDto(ReleaseNote releaseNote) {
        if ( releaseNote == null ) {
            return null;
        }

        ReleasesDataDtoBuilder releasesDataDto = ReleasesDataDto.builder();

        releasesDataDto.releaseId( releaseNote.getReleaseId() );
        releasesDataDto.version( releaseNote.getVersion() );
        releasesDataDto.summary( releaseNote.getSummary() );
        releasesDataDto.deployDate( releaseNote.getDeployDate() );
        releasesDataDto.deployStatus( releaseNote.getDeployStatus() );

        return releasesDataDto.build();
    }

    @Override
    public ReleaseInfoResponseDto toReleaseInfoResponseDto(ReleaseNote releaseNote) {
        if ( releaseNote == null ) {
            return null;
        }

        ReleaseInfoResponseDtoBuilder releaseInfoResponseDto = ReleaseInfoResponseDto.builder();

        releaseInfoResponseDto.releaseId( releaseNote.getReleaseId() );
        releaseInfoResponseDto.title( releaseNote.getTitle() );
        releaseInfoResponseDto.content( releaseNote.getContent() );
        releaseInfoResponseDto.summary( releaseNote.getSummary() );
        releaseInfoResponseDto.version( releaseNote.getVersion() );
        releaseInfoResponseDto.deployDate( releaseNote.getDeployDate() );
        releaseInfoResponseDto.deployStatus( releaseNote.getDeployStatus() );
        releaseInfoResponseDto.issues( issueListToConnectedIssuesDataDtoList( releaseNote.getIssues() ) );
        releaseInfoResponseDto.opinions( releaseOpinionListToReleaseOpinionsDataDtoList( releaseNote.getOpinions() ) );

        return releaseInfoResponseDto.build();
    }

    protected List<ConnectedIssuesDataDto> issueListToConnectedIssuesDataDtoList(List<Issue> list) {
        if ( list == null ) {
            return null;
        }

        List<ConnectedIssuesDataDto> list1 = new ArrayList<ConnectedIssuesDataDto>( list.size() );
        for ( Issue issue : list ) {
            list1.add( issueMapper.toConnectedIssuesDataDto( issue ) );
        }

        return list1;
    }

    protected List<ReleaseOpinionsDataDto> releaseOpinionListToReleaseOpinionsDataDtoList(List<ReleaseOpinion> list) {
        if ( list == null ) {
            return null;
        }

        List<ReleaseOpinionsDataDto> list1 = new ArrayList<ReleaseOpinionsDataDto>( list.size() );
        for ( ReleaseOpinion releaseOpinion : list ) {
            list1.add( releaseOpinionMapper.toReleaseOpinionsDataDto( releaseOpinion ) );
        }

        return list1;
    }
}
