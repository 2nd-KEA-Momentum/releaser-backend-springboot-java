package com.momentum.releaser.domain.issue.dao;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;


@Slf4j
@Repository
@RequiredArgsConstructor
public class IssueRepositoryImpl implements IssueRepositoryCustom{
    private final JPAQueryFactory queryFactory;

//    @Override
//    public List<IssueInfoRes> getIssues() {
//        QIssue issue = QIssue.issue;
//        QProjectMember member = QProjectMember.projectMember;
//        QUser user = QUser.user;
//        QReleaseNote releaseNote = QReleaseNote.releaseNote;
//
//        List<IssueInfoRes> fetch = queryFactory
//                .select(
//                        new QIssueResDto_IssueInfoRes(
//                                issue.issueId,
//                                issue.title,
//                                issue.content,
//                                member.memberId,
//                                user.name,
//                                user.img,
//                                issue.tag,
//                                releaseNote.version,
//                                issue.edit,
//                                issue.lifeCycle)
//                )
//                .from()
//                .where()
//                .fetch();
//        return null;
//    }
}
