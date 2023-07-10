package com.momentum.releaser.domain.release.dao.opinion;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ReleaseOpinionRepositoryImpl implements ReleaseOpinionRepositoryCustom {

    private final JPAQueryFactory queryFactory;
}
