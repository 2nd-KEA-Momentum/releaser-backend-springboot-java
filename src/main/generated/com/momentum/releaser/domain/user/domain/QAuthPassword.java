package com.momentum.releaser.domain.user.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAuthPassword is a Querydsl query type for AuthPassword
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAuthPassword extends EntityPathBase<AuthPassword> {

    private static final long serialVersionUID = 49075578L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAuthPassword authPassword = new QAuthPassword("authPassword");

    public final com.momentum.releaser.global.common.QBaseTime _super = new com.momentum.releaser.global.common.QBaseTime(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedDate = _super.modifiedDate;

    public final StringPath password = createString("password");

    public final NumberPath<Long> securityId = createNumber("securityId", Long.class);

    public final QUser user;

    public QAuthPassword(String variable) {
        this(AuthPassword.class, forVariable(variable), INITS);
    }

    public QAuthPassword(Path<? extends AuthPassword> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAuthPassword(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAuthPassword(PathMetadata metadata, PathInits inits) {
        this(AuthPassword.class, metadata, inits);
    }

    public QAuthPassword(Class<? extends AuthPassword> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new QUser(forProperty("user"), inits.get("user")) : null;
    }

}

