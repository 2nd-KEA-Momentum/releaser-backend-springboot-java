package com.momentum.releaser.domain.release.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QReleaseNote is a Querydsl query type for ReleaseNote
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QReleaseNote extends EntityPathBase<ReleaseNote> {

    private static final long serialVersionUID = -881831354L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QReleaseNote releaseNote = new QReleaseNote("releaseNote");

    public final com.momentum.releaser.global.common.QBaseTime _super = new com.momentum.releaser.global.common.QBaseTime(this);

    public final StringPath content = createString("content");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final DateTimePath<java.util.Date> deployDate = createDateTime("deployDate", java.util.Date.class);

    public final ListPath<com.momentum.releaser.domain.issue.domain.Issue, com.momentum.releaser.domain.issue.domain.QIssue> issues = this.<com.momentum.releaser.domain.issue.domain.Issue, com.momentum.releaser.domain.issue.domain.QIssue>createList("issues", com.momentum.releaser.domain.issue.domain.Issue.class, com.momentum.releaser.domain.issue.domain.QIssue.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedDate = _super.modifiedDate;

    public final ListPath<ReleaseOpinion, QReleaseOpinion> opinions = this.<ReleaseOpinion, QReleaseOpinion>createList("opinions", ReleaseOpinion.class, QReleaseOpinion.class, PathInits.DIRECT2);

    public final com.momentum.releaser.domain.project.domain.QProject project;

    public final NumberPath<Long> releaseId = createNumber("releaseId", Long.class);

    public final ComparablePath<Character> status = createComparable("status", Character.class);

    public final StringPath summary = createString("summary");

    public final StringPath title = createString("title");

    public final StringPath version = createString("version");

    public QReleaseNote(String variable) {
        this(ReleaseNote.class, forVariable(variable), INITS);
    }

    public QReleaseNote(Path<? extends ReleaseNote> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QReleaseNote(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QReleaseNote(PathMetadata metadata, PathInits inits) {
        this(ReleaseNote.class, metadata, inits);
    }

    public QReleaseNote(Class<? extends ReleaseNote> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.project = inits.isInitialized("project") ? new com.momentum.releaser.domain.project.domain.QProject(forProperty("project")) : null;
    }

}

