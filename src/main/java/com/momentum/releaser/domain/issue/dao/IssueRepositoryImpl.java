package com.momentum.releaser.domain.issue.dao;

import com.momentum.releaser.domain.issue.domain.IssueNum;
import com.momentum.releaser.domain.issue.domain.LifeCycle;
import com.momentum.releaser.domain.issue.domain.QIssue;
import com.momentum.releaser.domain.issue.domain.QIssueNum;
import com.momentum.releaser.domain.issue.dto.IssueResDto;
import com.momentum.releaser.domain.issue.dto.IssueResDto.GetConnectionIssues;
import com.momentum.releaser.domain.issue.dto.IssueResDto.GetDoneIssues;
import com.momentum.releaser.domain.issue.dto.IssueResDto.IssueInfoRes;
import com.momentum.releaser.domain.issue.dto.QIssueResDto_GetConnectionIssues;
import com.momentum.releaser.domain.issue.dto.QIssueResDto_GetDoneIssues;
import com.momentum.releaser.domain.issue.dto.QIssueResDto_IssueInfoRes;
import com.momentum.releaser.domain.project.domain.Project;
import com.momentum.releaser.domain.project.domain.QProjectMember;
import com.momentum.releaser.domain.release.domain.QReleaseNote;
import com.momentum.releaser.domain.release.domain.ReleaseNote;
import com.momentum.releaser.domain.user.domain.QUser;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringExpression;
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

    @Override
    public List<IssueInfoRes> getIssues(Project getProject) {
        QIssue issue = QIssue.issue;
        QIssueNum issueNum = QIssueNum.issueNum1;
        QProjectMember member = QProjectMember.projectMember;
        QUser user = QUser.user;
        QReleaseNote releaseNote = QReleaseNote.releaseNote;


        List<IssueInfoRes> result = queryFactory
                .select(
                        new QIssueResDto_IssueInfoRes(
                                issue.issueId,
                                issueNum.issueNum,
                                issue.title,
                                issue.content,
                                member.memberId,
                                user.name.as("memberName"),
                                user.img.as("memberImg"),
                                Expressions.stringTemplate("CAST({0} AS string)", issue.tag),
                                releaseNote.version.as("releaseVersion"),
                                issue.edit,
                                Expressions.stringTemplate("CAST({0} AS string)", issue.lifeCycle))
                )
                .from(issue)  // Issue 테이블을 지정
                .leftJoin(issue.issueNum, issueNum)
                .leftJoin(issue.member, member)
                .leftJoin(member.user, user)
                .leftJoin(issue.release, releaseNote)
                .where(issue.project.eq(getProject))
                .fetchResults().getResults();
        return result;
    }

    @Override
    public List<GetDoneIssues> getDoneIssues(Project getProject) {
        QIssue issue = QIssue.issue;
        QIssueNum issueNum = QIssueNum.issueNum1;
        QProjectMember member = QProjectMember.projectMember;
        QUser user = QUser.user;

        List<GetDoneIssues> reuslt = queryFactory
                .select(new QIssueResDto_GetDoneIssues(
                        issue.issueId,
                        issueNum.issueNum,
                        issue.title,
                        Expressions.stringTemplate("CAST({0} AS string)", issue.tag),
                        member.memberId,
                        user.name.as("memberName"),
                        user.img.as("memberImg"))
                )
                .from(issue)
                .leftJoin(issue.issueNum, issueNum)
                .leftJoin(issue.member, member)
                .leftJoin(member.user, user)
                .where(issue.project.eq(getProject)
                        .and(issue.lifeCycle.eq(LifeCycle.valueOf("Done")))
                        .and(issue.release.isNull()))
                .fetchResults().getResults();

        return reuslt;
    }

    @Override
    public List<GetConnectionIssues> getConnectionIssues(Project getProject, ReleaseNote getReleaseNote) {
        QIssue issue = QIssue.issue;
        QIssueNum issueNum = QIssueNum.issueNum1;
        QProjectMember member = QProjectMember.projectMember;
        QUser user = QUser.user;
        QReleaseNote releaseNote = QReleaseNote.releaseNote;

        List<GetConnectionIssues> reuslt = queryFactory
                .select(new QIssueResDto_GetConnectionIssues(
                        issue.issueId,
                        issueNum.issueNum,
                        issue.title,
                        Expressions.stringTemplate("CAST({0} AS string)", issue.tag),
                        issue.edit,
                        member.memberId,
                        user.name.as("memberName"),
                        user.img.as("memberImg"),
                        releaseNote.version)
                )
                .from(issue)  // Issue 테이블을 지정
                .leftJoin(issue.issueNum, issueNum)
                .leftJoin(issue.member, member)
                .leftJoin(member.user, user)
                .leftJoin(issue.release, releaseNote)
                .where(issue.project.eq(getProject)
                        .and(issue.release.eq(getReleaseNote)))
                .fetchResults().getResults();

        return reuslt;
    }
}
