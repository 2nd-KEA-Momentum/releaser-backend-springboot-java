package com.momentum.releaser.domain.release.dao.approval;

import static com.momentum.releaser.domain.release.domain.QReleaseApproval.releaseApproval;

import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.querydsl.jpa.impl.JPAQueryFactory;

import com.momentum.releaser.domain.release.domain.ReleaseNote;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ReleaseApprovalRepositoryImpl implements ReleaseApprovalCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public void deleteByReleaseNote(ReleaseNote releaseNote) {
        queryFactory
                .delete(releaseApproval)
                .where(releaseApproval.release.eq(releaseNote))
                .execute();
    }

    @Override
    public void deleteByReleaseApproval() {
        queryFactory
                .delete(releaseApproval)
                .where(releaseApproval.release.isNull()
                        .or(releaseApproval.member.isNull()))
                .execute();
    }
}
