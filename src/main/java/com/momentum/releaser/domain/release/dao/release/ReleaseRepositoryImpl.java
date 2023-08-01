package com.momentum.releaser.domain.release.dao.release;

import static com.momentum.releaser.domain.release.domain.QReleaseNote.releaseNote;

import java.util.List;

import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.querydsl.jpa.impl.JPAQueryFactory;

import com.momentum.releaser.domain.project.domain.Project;
import com.momentum.releaser.domain.release.domain.ReleaseNote;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ReleaseRepositoryImpl implements ReleaseRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    /**
     * 변경하려는 버전이 해당 프로젝트 내에 이미 존재하는 버전인지 확인한다.
     *
     * @author seonwoo
     * @date 2023-07-07
     * @param project 확인하려는 버전이 속한 프로젝트
     * @param releaseId 수정하려는 릴리즈 노트의 식별 번호 (새로 추가하는 경우는 null)
     * @param version 확인하려는 버전 값
     * @return boolean 해당 프로젝트 내에 이미 해당 버전이 존재하는지 여부
     */
    @Override
    public boolean existsByProjectAndVersion(Project project, Long releaseId, String version) {
        return queryFactory
                .selectFrom(releaseNote)
                .where(releaseNote.project.eq(project))
                .where(releaseNote.releaseId.ne(releaseId))
                .where(releaseNote.version.eq(version))
                .fetchFirst() != null;
    }

    /**
     * 특정 프로젝트의 모든 릴리즈 노트 버전을 가져온다.
     *
     * @author seonwoo
     * @date 2023-07-14
     * @param project 버전을 가져올 프로젝트
     * @return 해당 프로젝트의 모든 릴리즈 노트 버전 목록
     */
    @Override
    public List<String> findAllVersionsByProject(Project project) {
        return queryFactory
                .select(releaseNote.version)
                .from(releaseNote)
                .where(releaseNote.project.eq(project))
                .fetch();
    }

    /**
     * 수정하려는 릴리즈의 기존 버전 값을 뺀 나머지를 전달한다.
     *
     * @author seonwoo
     * @date 2023-07-06
     * @param project 프로젝트
     * @param version 제외하려는 버전
     * @return ReleaseNote 주어진 프로젝트에서 특정 버전을 제외한 모든 릴리즈 노트 리스트
     */
    @Override
    public List<ReleaseNote> findByProjectAndNotInVersion(Project project, String version) {
        return queryFactory
                .selectFrom(releaseNote)
                .where(releaseNote.project.eq(project))
                .where(releaseNote.version.ne(version))
                .fetch();
    }

    /**
     * 수정하려는 릴리즈 노트의 이전 릴리즈 노트들을 가져온다.
     *
     * @author seonwoo
     * @date 2023-07-09
     * @param project 프로젝트
     * @param version 기준 버전
     * @return ReleaseNote 주어진 프로젝트에서 기준 버전보다 낮은 이전 릴리즈 노트들을 내림차순으로 정렬
     */
    @Override
    public List<ReleaseNote> findPreviousReleaseNotes(Project project, String version) {
        // 같은 프로젝트 안의 모든 릴리즈 노트 중 전달받은 버전보다 낮은 버전의 릴리즈 노트 목록을 가져온다.
        return queryFactory
                .selectFrom(releaseNote)
                .where(releaseNote.project.eq(project))
                .where(releaseNote.version.lt(version))
                .orderBy(releaseNote.version.desc())
                .fetch();
    }
}
