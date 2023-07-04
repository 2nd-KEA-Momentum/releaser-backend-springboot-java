package com.momentum.releaser.domain.release.mapper;

import com.momentum.releaser.domain.release.domain.ReleaseNote;
import com.momentum.releaser.domain.release.dto.ReleaseDataDto.ReleasesDataDto;
import com.momentum.releaser.domain.release.dto.ReleaseDataDto.ReleasesDataDto.ReleasesDataDtoBuilder;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-07-04T21:43:22+0900",
    comments = "version: 1.4.2.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.1.1.jar, environment: Java 19.0.2 (Oracle Corporation)"
)
public class ReleaseMapperImpl implements ReleaseMapper {

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
}
