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
     * 수정하려는 릴리즈의 기존 버전 값을 뺀 나머지를 전달한다.
     */
    @Override
    public List<ReleaseNote> findByProjectAndNotInVersion(Project project, String version) {
        return queryFactory
                .selectFrom(releaseNote)
                .where(releaseNote.project.eq(project))
                .where(releaseNote.version.ne(version))
                .fetch();
    }
}
