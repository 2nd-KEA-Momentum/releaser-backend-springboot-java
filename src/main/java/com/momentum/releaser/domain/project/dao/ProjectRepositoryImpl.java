package com.momentum.releaser.domain.project.dao;

import com.momentum.releaser.domain.project.domain.Project;
import com.momentum.releaser.domain.project.domain.QProjectMember;
import com.momentum.releaser.domain.project.dto.ProjectResDto.GetMembersRes;
import com.momentum.releaser.domain.project.dto.QProjectResDto_GetMembersRes;
import com.momentum.releaser.domain.user.domain.QUser;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ProjectRepositoryImpl implements ProjectRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<GetMembersRes> getMemberList(Project project) {
        QProjectMember member = QProjectMember.projectMember;
        QUser user = QUser.user;

        List<GetMembersRes> getMembersRes = queryFactory
                .select(new QProjectResDto_GetMembersRes(
                        member.memberId,
                        user.userId,
                        user.name,
                        user.img,
                        member.position
                ))
                .from(member)
                .leftJoin(member.user, user)
                .where(member.project.eq(project))
                .fetch();
        return getMembersRes;
    }
}
