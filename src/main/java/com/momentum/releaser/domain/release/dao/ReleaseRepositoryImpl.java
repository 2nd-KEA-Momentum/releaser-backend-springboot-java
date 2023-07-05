package com.momentum.releaser.domain.release.dao;

import com.momentum.releaser.domain.project.domain.Project;
import com.momentum.releaser.domain.release.domain.ReleaseNote;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import static com.momentum.releaser.domain.release.domain.QReleaseNote.releaseNote;

@RequiredArgsConstructor
public class ReleaseRepositoryImpl implements ReleaseRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public ReleaseNote findLatestReleaseNote(Project project) {
        return jpaQueryFactory.selectFrom(releaseNote)
                .where(releaseNote.project.eq(project))
                .limit(1)
                .fetchOne();
    }
}
