package com.momentum.releaser.domain.issue.dao;

import com.momentum.releaser.domain.issue.domain.*;
import com.momentum.releaser.domain.issue.dto.IssueResponseDto.ConnectionIssuesResponseDTO;
import com.momentum.releaser.domain.issue.dto.IssueResponseDto.DoneIssuesResponseDTO;
import com.momentum.releaser.domain.issue.dto.IssueResponseDto.IssueInfoResponseDTO;
import com.momentum.releaser.domain.issue.dto.IssueResponseDto.OpinionInfoResponseDTO;
import com.momentum.releaser.domain.issue.dto.QIssueResponseDto_ConnectionIssuesResponseDTO;
import com.momentum.releaser.domain.issue.dto.QIssueResponseDto_DoneIssuesResponseDTO;
import com.momentum.releaser.domain.issue.dto.QIssueResponseDto_IssueInfoResponseDTO;
import com.momentum.releaser.domain.issue.dto.QIssueResponseDto_OpinionInfoResponseDTO;
import com.momentum.releaser.domain.project.domain.Project;
import com.momentum.releaser.domain.project.domain.QProjectMember;
import com.momentum.releaser.domain.release.domain.QReleaseNote;
import com.momentum.releaser.domain.release.domain.ReleaseNote;
import com.momentum.releaser.domain.user.domain.QUser;
import com.querydsl.core.types.dsl.Expressions;
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

        Optional<Long> result = Optional.ofNullable(queryFactory.
                select(issueNum.issueNum.max())
                .from(issueNum)
                .where(
                        issueNum.project.eq(project)
                )
                .limit(1)
                .fetchOne());

        Long number = 0L;
        if (result.isPresent()) {
            number = result.get();
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
    public List<IssueInfoResponseDTO> getIssues(Project getProject) {
        QIssue issue = QIssue.issue;
        QProjectMember member = QProjectMember.projectMember;
        QUser user = QUser.user;
        QReleaseNote releaseNote = QReleaseNote.releaseNote;

        List<IssueInfoResponseDTO> result = queryFactory
                .select(new QIssueResponseDto_IssueInfoResponseDTO(
                                issue.issueId,
                                issue.issueNum.issueNum,
                                issue.title,
                                issue.content,
                                issue.endDate,
                                member.memberId,
                                user.name.as("memberName"),
                                user.img.as("memberImg"),
                                Expressions.stringTemplate("CAST({0} AS string)", issue.tag),
                                releaseNote.version.as("releaseVersion"),
                                issue.edit,
                                Expressions.stringTemplate("CAST({0} AS string)", issue.lifeCycle))
                )
                .from(issue)
                .leftJoin(issue.member, member)
                .leftJoin(member.user, user)
                .leftJoin(issue.release, releaseNote)
                .where(issue.project.eq(getProject))
                .fetchResults().getResults();

        return result;
    }

    @Override
    public List<DoneIssuesResponseDTO> getDoneIssues(Project getProject, String status) {
        QIssue issue = QIssue.issue;
        QProjectMember member = QProjectMember.projectMember;
        QUser user = QUser.user;

        List<DoneIssuesResponseDTO> getDoneIssues = queryFactory
                .select(new QIssueResponseDto_DoneIssuesResponseDTO(
                        issue.issueId,
                        issue.issueNum.issueNum,
                        issue.title,
                        Expressions.stringTemplate("CAST({0} AS string)", issue.tag),
                        issue.endDate,
                        issue.edit,
                        Expressions.cases().when(member.status.eq('N')).then(0L).otherwise(member.memberId),
                        user.name.as("memberName"),
                        user.img.as("memberImg"))
                )
                .from(issue)
                .leftJoin(issue.member, member)
                .leftJoin(member.user, user)
                .where(issue.project.eq(getProject)
                        .and(issue.lifeCycle.eq(LifeCycle.valueOf(status)))
                        .and(issue.release.isNull()))
                .fetchResults().getResults();

        return getDoneIssues;
    }

    @Override
    public List<ConnectionIssuesResponseDTO> getConnectionIssues(Project getProject, ReleaseNote getReleaseNote) {
        QIssue issue = QIssue.issue;
        QProjectMember member = QProjectMember.projectMember;
        QUser user = QUser.user;
        QReleaseNote releaseNote = QReleaseNote.releaseNote;

        List<ConnectionIssuesResponseDTO> getConnectionIssues = queryFactory
                .select(new QIssueResponseDto_ConnectionIssuesResponseDTO(
                        issue.issueId,
                        issue.issueNum.issueNum,
                        issue.title,
                        Expressions.stringTemplate("CAST({0} AS string)", issue.tag),
                        issue.edit,
                        Expressions.cases().when(member.status.eq('N')).then(0L).otherwise(member.memberId),
                        user.name.as("memberName"),
                        user.img.as("memberImg"),
                        releaseNote.version)
                )
                .from(issue)  // Issue 테이블을 지정
                .leftJoin(issue.member, member)
                .leftJoin(member.user, user)
                .leftJoin(issue.release, releaseNote)
                .where(issue.project.eq(getProject)
                        .and(issue.release.eq(getReleaseNote)))
                .fetchResults().getResults();

        return getConnectionIssues;
    }

    @Override
    public List<OpinionInfoResponseDTO> getIssueOpinion(Issue issue) {
        QIssueOpinion issueOpinion = QIssueOpinion.issueOpinion;
        QProjectMember member = QProjectMember.projectMember;
        QUser user = QUser.user;

        List<OpinionInfoResponseDTO> opinionInfoRes = queryFactory
                .select(new QIssueResponseDto_OpinionInfoResponseDTO(
                        Expressions.cases().when(issueOpinion.member.status.eq('N')).then(0L).otherwise(issueOpinion.member.memberId),
                        user.name.as("memberName"),
                        user.img.as("memberImg"),
                        issueOpinion.issueOpinionId.as("opinionId"),
                        issueOpinion.opinion
                ))
                .from(issueOpinion)
                .leftJoin(issueOpinion.member, member)
                .leftJoin(issueOpinion.member.user, user)
                .where(issueOpinion.issue.eq(issue))
                .fetchResults().getResults();

        return opinionInfoRes;
    }

}
