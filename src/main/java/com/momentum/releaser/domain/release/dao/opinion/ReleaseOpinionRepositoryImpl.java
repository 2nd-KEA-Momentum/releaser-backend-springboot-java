package com.momentum.releaser.domain.release.dao.opinion;

import com.momentum.releaser.domain.release.domain.ReleaseNote;
import com.momentum.releaser.domain.release.dto.QReleaseDataDto_ReleaseOpinionsDataDTO;
import com.momentum.releaser.domain.release.dto.ReleaseDataDto.ReleaseOpinionsDataDTO;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.momentum.releaser.domain.project.domain.QProjectMember.projectMember;
import static com.momentum.releaser.domain.release.domain.QReleaseOpinion.releaseOpinion;
import static com.momentum.releaser.domain.user.domain.QUser.user;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ReleaseOpinionRepositoryImpl implements ReleaseOpinionRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    /**
     * 릴리즈 노트의 의견 목록을 가져올 때 DTO로 변환해서 반환한다.
     */
    @Override
    public List<ReleaseOpinionsDataDTO> getDtosByReleaseNote(ReleaseNote releaseNote) {

        return queryFactory
                .select(new QReleaseDataDto_ReleaseOpinionsDataDTO(
                        releaseOpinion.releaseOpinionId,
                        releaseOpinion.opinion,
                        Expressions.cases().when(releaseOpinion.member.status.eq('N'))
                                .then(0L)
                                .otherwise(releaseOpinion.member.memberId),
                        releaseOpinion.member.user.name.as("memberName"),
                        releaseOpinion.member.user.img.as("memberProfileImg")
                ))
                .from(releaseOpinion)
                .leftJoin(releaseOpinion.member, projectMember)
                .leftJoin(releaseOpinion.member.user, user)
                .where(releaseOpinion.release.eq(releaseNote))
                .fetch();
    }
}
