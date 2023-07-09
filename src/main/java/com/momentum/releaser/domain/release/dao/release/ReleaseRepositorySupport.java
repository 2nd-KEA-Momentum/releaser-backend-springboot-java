package com.momentum.releaser.domain.release.dao.release;

import com.momentum.releaser.domain.release.domain.ReleaseNote;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

@Repository
public class ReleaseRepositorySupport extends QuerydslRepositorySupport {
    private final JPAQueryFactory jpaQueryFactory;

    public ReleaseRepositorySupport(JPAQueryFactory jpaQueryFactory) {
        super(ReleaseNote.class);
        this.jpaQueryFactory = jpaQueryFactory;
    }
}
