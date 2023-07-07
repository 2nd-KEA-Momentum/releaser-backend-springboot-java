package com.momentum.releaser.domain.issue.dao;

import com.momentum.releaser.domain.issue.domain.IssueNum;
import com.momentum.releaser.domain.issue.domain.QIssue;
import com.momentum.releaser.domain.issue.domain.QIssueNum;
import com.momentum.releaser.domain.issue.dto.IssueResDto.IssueInfoRes;
import com.momentum.releaser.domain.issue.dto.QIssueResDto_IssueInfoRes;
import com.momentum.releaser.domain.project.domain.Project;
import com.momentum.releaser.domain.project.domain.QProjectMember;
import com.momentum.releaser.domain.release.domain.QReleaseNote;
import com.momentum.releaser.domain.user.domain.QUser;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


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
                .from(issue)  // Issue 테이블을 지정
                .leftJoin(issue.member, member)
                .leftJoin(member.user, user)
                .leftJoin(issue.release, releaseNote)
                .fetch();
        return null;
    }

    @Override
    public Long getIssueNum(Project project) {
        QIssueNum issueNum = QIssueNum.issueNum1;
        Optional<IssueNum> result = Optional.ofNullable(queryFactory.
                selectFrom(issueNum)
                .where(
                        issueNum.project.eq(project)
                )
                .limit(1)
                .fetchOne());

        Long number = 0L;
        if (result.isPresent()) {
            number = result.get().getIssueNum();
        }
        return number;
    }

    @Override
    public void deleteByIssueNum() {
        QIssueNum issueNum = QIssueNum.issueNum1;

        queryFactory
                .delete(issueNum)
                .where(issueNum.project.isNull()
                        .or(issueNum.issue.isNull()))
                .execute();
    }
}
