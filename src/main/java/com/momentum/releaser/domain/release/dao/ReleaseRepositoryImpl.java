package com.momentum.releaser.domain.release.dao;

import com.momentum.releaser.domain.project.domain.Project;
import com.momentum.releaser.domain.release.domain.ReleaseNote;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.momentum.releaser.domain.release.domain.QReleaseNote.releaseNote;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ReleaseRepositoryImpl implements ReleaseRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<ReleaseNote> findByProject(Project project) {
        ReleaseNote foundReleaseNote = queryFactory
                .selectFrom(releaseNote)
                .where(releaseNote.project.eq(project))
                .limit(1)
                .fetchOne();

        log.info("foundReleaseNote: {}", foundReleaseNote);

        return Optional.ofNullable(foundReleaseNote);
    }
}
