package com.momentum.releaser.domain.user.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUser is a Querydsl query type for User
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUser extends EntityPathBase<User> {

    private static final long serialVersionUID = -298322974L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUser user = new QUser("user");

    public final com.momentum.releaser.global.common.QBaseTime _super = new com.momentum.releaser.global.common.QBaseTime(this);

    public final QAuthPassword authPassword;

    public final QAuthSocial authSocial;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final StringPath email = createString("email");

    public final StringPath img = createString("img");

    public final ListPath<com.momentum.releaser.domain.project.domain.ProjectMember, com.momentum.releaser.domain.project.domain.QProjectMember> members = this.<com.momentum.releaser.domain.project.domain.ProjectMember, com.momentum.releaser.domain.project.domain.QProjectMember>createList("members", com.momentum.releaser.domain.project.domain.ProjectMember.class, com.momentum.releaser.domain.project.domain.QProjectMember.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedDate = _super.modifiedDate;

    public final StringPath name = createString("name");

    public final ComparablePath<Character> status = createComparable("status", Character.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QUser(String variable) {
        this(User.class, forVariable(variable), INITS);
    }

    public QUser(Path<? extends User> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUser(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUser(PathMetadata metadata, PathInits inits) {
        this(User.class, metadata, inits);
    }

    public QUser(Class<? extends User> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.authPassword = inits.isInitialized("authPassword") ? new QAuthPassword(forProperty("authPassword"), inits.get("authPassword")) : null;
        this.authSocial = inits.isInitialized("authSocial") ? new QAuthSocial(forProperty("authSocial"), inits.get("authSocial")) : null;
    }

}

