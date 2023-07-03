package com.momentum.releaser.domain.project.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProject is a Querydsl query type for Project
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProject extends EntityPathBase<Project> {

    private static final long serialVersionUID = 228172276L;

    public static final QProject project = new QProject("project");

    public final com.momentum.releaser.global.common.QBaseTime _super = new com.momentum.releaser.global.common.QBaseTime(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final StringPath img = createString("img");

    public final ListPath<com.momentum.releaser.domain.issue.domain.Issue, com.momentum.releaser.domain.issue.domain.QIssue> issues = this.<com.momentum.releaser.domain.issue.domain.Issue, com.momentum.releaser.domain.issue.domain.QIssue>createList("issues", com.momentum.releaser.domain.issue.domain.Issue.class, com.momentum.releaser.domain.issue.domain.QIssue.class, PathInits.DIRECT2);

    public final ListPath<ProjectMember, QProjectMember> members = this.<ProjectMember, QProjectMember>createList("members", ProjectMember.class, QProjectMember.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedDate = _super.modifiedDate;

    public final NumberPath<Long> projectId = createNumber("projectId", Long.class);

    public final ListPath<com.momentum.releaser.domain.release.domain.ReleaseNote, com.momentum.releaser.domain.release.domain.QReleaseNote> releases = this.<com.momentum.releaser.domain.release.domain.ReleaseNote, com.momentum.releaser.domain.release.domain.QReleaseNote>createList("releases", com.momentum.releaser.domain.release.domain.ReleaseNote.class, com.momentum.releaser.domain.release.domain.QReleaseNote.class, PathInits.DIRECT2);

    public final ComparablePath<Character> status = createComparable("status", Character.class);

    public final StringPath team = createString("team");

    public final StringPath title = createString("title");

    public QProject(String variable) {
        super(Project.class, forVariable(variable));
    }

    public QProject(Path<? extends Project> path) {
        super(path.getType(), path.getMetadata());
    }

    public QProject(PathMetadata metadata) {
        super(Project.class, metadata);
    }

}

