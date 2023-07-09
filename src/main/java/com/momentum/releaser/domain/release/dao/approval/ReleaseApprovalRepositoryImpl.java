package com.momentum.releaser.domain.release.dao.approval;

import com.momentum.releaser.domain.release.domain.ReleaseNote;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import static com.momentum.releaser.domain.release.domain.QReleaseApproval.releaseApproval;

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
}
