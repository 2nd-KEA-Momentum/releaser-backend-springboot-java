package com.momentum.releaser.domain.release.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QReleaseOpinion is a Querydsl query type for ReleaseOpinion
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QReleaseOpinion extends EntityPathBase<ReleaseOpinion> {

    private static final long serialVersionUID = -1711538002L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QReleaseOpinion releaseOpinion = new QReleaseOpinion("releaseOpinion");

    public final com.momentum.releaser.global.common.QBaseTime _super = new com.momentum.releaser.global.common.QBaseTime(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final com.momentum.releaser.domain.issue.domain.QIssue issue;

    public final com.momentum.releaser.domain.project.domain.QProjectMember member;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedDate = _super.modifiedDate;

    public final StringPath opinion = createString("opinion");

    public final QReleaseNote release;

    public final NumberPath<Long> releaseOpinionId = createNumber("releaseOpinionId", Long.class);

    public final ComparablePath<Character> status = createComparable("status", Character.class);

    public QReleaseOpinion(String variable) {
        this(ReleaseOpinion.class, forVariable(variable), INITS);
    }

    public QReleaseOpinion(Path<? extends ReleaseOpinion> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QReleaseOpinion(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QReleaseOpinion(PathMetadata metadata, PathInits inits) {
        this(ReleaseOpinion.class, metadata, inits);
    }

    public QReleaseOpinion(Class<? extends ReleaseOpinion> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.issue = inits.isInitialized("issue") ? new com.momentum.releaser.domain.issue.domain.QIssue(forProperty("issue"), inits.get("issue")) : null;
        this.member = inits.isInitialized("member") ? new com.momentum.releaser.domain.project.domain.QProjectMember(forProperty("member"), inits.get("member")) : null;
        this.release = inits.isInitialized("release") ? new QReleaseNote(forProperty("release"), inits.get("release")) : null;
    }

}

