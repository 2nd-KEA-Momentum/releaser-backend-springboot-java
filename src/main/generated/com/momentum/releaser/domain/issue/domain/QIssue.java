package com.momentum.releaser.domain.issue.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QIssue is a Querydsl query type for Issue
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QIssue extends EntityPathBase<Issue> {

    private static final long serialVersionUID = 931848372L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QIssue issue = new QIssue("issue");

    public final com.momentum.releaser.global.common.QBaseTime _super = new com.momentum.releaser.global.common.QBaseTime(this);

    public final StringPath content = createString("content");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final DateTimePath<java.util.Date> endDate = createDateTime("endDate", java.util.Date.class);

    public final NumberPath<Long> issueId = createNumber("issueId", Long.class);

    public final StringPath lifeCycle = createString("lifeCycle");

    public final com.momentum.releaser.domain.project.domain.QProjectMember member;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedDate = _super.modifiedDate;

    public final ListPath<com.momentum.releaser.domain.release.domain.ReleaseOpinion, com.momentum.releaser.domain.release.domain.QReleaseOpinion> opinions = this.<com.momentum.releaser.domain.release.domain.ReleaseOpinion, com.momentum.releaser.domain.release.domain.QReleaseOpinion>createList("opinions", com.momentum.releaser.domain.release.domain.ReleaseOpinion.class, com.momentum.releaser.domain.release.domain.QReleaseOpinion.class, PathInits.DIRECT2);

    public final com.momentum.releaser.domain.project.domain.QProject project;

    public final com.momentum.releaser.domain.release.domain.QReleaseNote release;

    public final ComparablePath<Character> status = createComparable("status", Character.class);

    public final StringPath tag = createString("tag");

    public final StringPath title = createString("title");

    public QIssue(String variable) {
        this(Issue.class, forVariable(variable), INITS);
    }

    public QIssue(Path<? extends Issue> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QIssue(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QIssue(PathMetadata metadata, PathInits inits) {
        this(Issue.class, metadata, inits);
    }

    public QIssue(Class<? extends Issue> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new com.momentum.releaser.domain.project.domain.QProjectMember(forProperty("member"), inits.get("member")) : null;
        this.project = inits.isInitialized("project") ? new com.momentum.releaser.domain.project.domain.QProject(forProperty("project")) : null;
        this.release = inits.isInitialized("release") ? new com.momentum.releaser.domain.release.domain.QReleaseNote(forProperty("release"), inits.get("release")) : null;
    }

}

