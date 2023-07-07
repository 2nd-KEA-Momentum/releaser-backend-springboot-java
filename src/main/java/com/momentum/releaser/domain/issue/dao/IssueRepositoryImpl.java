package com.momentum.releaser.domain.issue.dao;

import com.momentum.releaser.domain.issue.domain.QIssue;
import com.momentum.releaser.domain.issue.dto.IssueResDto.IssueInfoRes;
import com.momentum.releaser.domain.issue.dto.QIssueResDto_IssueInfoRes;
import com.momentum.releaser.domain.project.domain.QProjectMember;
import com.momentum.releaser.domain.release.domain.QReleaseNote;
import com.momentum.releaser.domain.user.domain.QUser;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;


@Slf4j
@Repository
@RequiredArgsConstructor
public class IssueRepositoryImpl implements IssueRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Override
    public List<IssueInfoRes> getIssues() {
        QIssue issue = QIssue.issue;
        QProjectMember member = QProjectMember.projectMember;
        QUser user = QUser.user;
        QReleaseNote releaseNote = QReleaseNote.releaseNote;


        List<IssueInfoRes> fetch = queryFactory
                .select(
                        new QIssueResDto_IssueInfoRes(
                                issue.issueId,
                                issue.title,
                                issue.content,
                                member.memberId,
                                user.name,
                                user.img,
                                issue.tag.stringValue(),
                                releaseNote.version,
                                issue.edit,
                                issue.lifeCycle.stringValue())
                )
                .from()
                .where()
                .fetch();
        return null;
    }
}
