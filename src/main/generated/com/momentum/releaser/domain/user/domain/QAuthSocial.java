package com.momentum.releaser.domain.user.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAuthSocial is a Querydsl query type for AuthSocial
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAuthSocial extends EntityPathBase<AuthSocial> {

    private static final long serialVersionUID = 308436492L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAuthSocial authSocial = new QAuthSocial("authSocial");

    public final com.momentum.releaser.global.common.QBaseTime _super = new com.momentum.releaser.global.common.QBaseTime(this);

    public final NumberPath<Long> authId = createNumber("authId", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedDate = _super.modifiedDate;

    public final StringPath token = createString("token");

    public final StringPath type = createString("type");

    public final QUser user;

    public QAuthSocial(String variable) {
        this(AuthSocial.class, forVariable(variable), INITS);
    }

    public QAuthSocial(Path<? extends AuthSocial> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAuthSocial(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAuthSocial(PathMetadata metadata, PathInits inits) {
        this(AuthSocial.class, metadata, inits);
    }

    public QAuthSocial(Class<? extends AuthSocial> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new QUser(forProperty("user"), inits.get("user")) : null;
    }

}

