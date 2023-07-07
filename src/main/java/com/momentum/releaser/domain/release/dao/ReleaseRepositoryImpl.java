package com.momentum.releaser.domain.release.dao;

import com.momentum.releaser.domain.project.domain.Project;
import com.momentum.releaser.domain.release.domain.ReleaseNote;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.momentum.releaser.domain.release.domain.QReleaseNote.releaseNote;


@Slf4j
@Repository
@RequiredArgsConstructor
public class ReleaseRepositoryImpl implements ReleaseRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    /**
     * 가장 최신의 버전을 가져온다.
     */
    @Override
    public Optional<ReleaseNote> findTopByProject(Project project) {
        ReleaseNote foundReleaseNote = queryFactory
                .selectFrom(releaseNote)
                .where(releaseNote.project.eq(project))
                .limit(1)
                .fetchOne();

        return Optional.ofNullable(foundReleaseNote);
    }

    /**
     * 버전을 수정할 때 바로 이전 버전을 가져온다.
     */
    @Override
    public Optional<ReleaseNote> findTop2ByProject(Project project) {
        List<ReleaseNote> foundReleaseNotes = queryFactory
                .selectFrom(releaseNote)
                .where(releaseNote.project.eq(project))
                .limit(2)
                .fetch();

        return Optional.ofNullable(foundReleaseNotes.get(1));
    }
}
