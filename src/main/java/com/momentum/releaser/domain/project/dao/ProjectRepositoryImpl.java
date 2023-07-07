package com.momentum.releaser.domain.project.dao;

import com.momentum.releaser.domain.issue.domain.QIssueNum;
import com.momentum.releaser.domain.project.domain.Project;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ProjectRepositoryImpl implements ProjectRepositoryCustom{

    private final JPAQueryFactory queryFactory;


}
