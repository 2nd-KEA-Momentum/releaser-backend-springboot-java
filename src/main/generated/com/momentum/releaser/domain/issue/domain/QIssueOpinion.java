package com.momentum.releaser.domain.issue.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QIssueOpinion is a Querydsl query type for IssueOpinion
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QIssueOpinion extends EntityPathBase<IssueOpinion> {

    private static final long serialVersionUID = 1123219502L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QIssueOpinion issueOpinion = new QIssueOpinion("issueOpinion");

    public final com.momentum.releaser.global.common.QBaseTime _super = new com.momentum.releaser.global.common.QBaseTime(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final NumberPath<Long> issueOpinionId = createNumber("issueOpinionId", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedDate = _super.modifiedDate;

    public final StringPath opinion = createString("opinion");

    public final com.momentum.releaser.domain.project.domain.QProjectMember projectMember;

    public final ComparablePath<Character> status = createComparable("status", Character.class);

    public QIssueOpinion(String variable) {
        this(IssueOpinion.class, forVariable(variable), INITS);
    }

    public QIssueOpinion(Path<? extends IssueOpinion> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QIssueOpinion(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QIssueOpinion(PathMetadata metadata, PathInits inits) {
        this(IssueOpinion.class, metadata, inits);
    }

    public QIssueOpinion(Class<? extends IssueOpinion> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.projectMember = inits.isInitialized("projectMember") ? new com.momentum.releaser.domain.project.domain.QProjectMember(forProperty("projectMember"), inits.get("projectMember")) : null;
    }

}

