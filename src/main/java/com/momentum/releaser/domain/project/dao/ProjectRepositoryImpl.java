package com.momentum.releaser.domain.project.dao;

import com.momentum.releaser.domain.project.domain.Project;
import com.momentum.releaser.domain.project.domain.ProjectMember;
import com.momentum.releaser.domain.project.domain.QProject;
import com.momentum.releaser.domain.project.domain.QProjectMember;
import com.momentum.releaser.domain.project.dto.ProjectResDto.GetMembersRes;
import com.momentum.releaser.domain.project.dto.QProjectResDto_GetMembersRes;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ProjectRepositoryImpl implements ProjectRepositoryCustom{

    private final JPAQueryFactory queryFactory;


    @Override
    public List<GetMembersRes> getMembersByProject(Project getProject) {
        QProjectMember member = QProjectMember.projectMember;

        List<ProjectMember> projectMembers = queryFactory
                .select(member)
                .from(member)
                .where(member.project.eq(getProject))
                .fetch();

        List<GetMembersRes> membersRes = projectMembers.stream()
                .map(projectMember -> new GetMembersRes(
                        projectMember.getMemberId(),
                        projectMember.getUser().getUserId(),
                        projectMember.getUser().getName(),
                        projectMember.getUser().getImg(),
                        projectMember.getPosition()
                ))
                .collect(Collectors.toList());


        return membersRes;
    }
}
