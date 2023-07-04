package com.momentum.releaser.domain.project.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProjectMember is a Querydsl query type for ProjectMember
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProjectMember extends EntityPathBase<ProjectMember> {

    private static final long serialVersionUID = 1991561646L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProjectMember projectMember = new QProjectMember("projectMember");

    public final com.momentum.releaser.global.common.QBaseTime _super = new com.momentum.releaser.global.common.QBaseTime(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final NumberPath<Long> memberId = createNumber("memberId", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedDate = _super.modifiedDate;

    public final ListPath<com.momentum.releaser.domain.release.domain.ReleaseOpinion, com.momentum.releaser.domain.release.domain.QReleaseOpinion> opinions = this.<com.momentum.releaser.domain.release.domain.ReleaseOpinion, com.momentum.releaser.domain.release.domain.QReleaseOpinion>createList("opinions", com.momentum.releaser.domain.release.domain.ReleaseOpinion.class, com.momentum.releaser.domain.release.domain.QReleaseOpinion.class, PathInits.DIRECT2);

    public final ComparablePath<Character> position = createComparable("position", Character.class);

    public final QProject project;

    public final ComparablePath<Character> status = createComparable("status", Character.class);

    public final com.momentum.releaser.domain.user.domain.QUser user;

    public QProjectMember(String variable) {
        this(ProjectMember.class, forVariable(variable), INITS);
    }

    public QProjectMember(Path<? extends ProjectMember> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProjectMember(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProjectMember(PathMetadata metadata, PathInits inits) {
        this(ProjectMember.class, metadata, inits);
    }

    public QProjectMember(Class<? extends ProjectMember> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.project = inits.isInitialized("project") ? new QProject(forProperty("project")) : null;
        this.user = inits.isInitialized("user") ? new com.momentum.releaser.domain.user.domain.QUser(forProperty("user"), inits.get("user")) : null;
    }

}

