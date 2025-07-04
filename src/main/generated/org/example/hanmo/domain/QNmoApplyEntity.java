package org.example.hanmo.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QNmoApplyEntity is a Querydsl query type for NmoApplyEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QNmoApplyEntity extends EntityPathBase<NmoApplyEntity> {

    private static final long serialVersionUID = -1096311162L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QNmoApplyEntity nmoApplyEntity = new QNmoApplyEntity("nmoApplyEntity");

    public final QBaseTimeEntity _super = new QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createDate = _super.createDate;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QNmoEntity nmo;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final QUserEntity user;

    public QNmoApplyEntity(String variable) {
        this(NmoApplyEntity.class, forVariable(variable), INITS);
    }

    public QNmoApplyEntity(Path<? extends NmoApplyEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QNmoApplyEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QNmoApplyEntity(PathMetadata metadata, PathInits inits) {
        this(NmoApplyEntity.class, metadata, inits);
    }

    public QNmoApplyEntity(Class<? extends NmoApplyEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.nmo = inits.isInitialized("nmo") ? new QNmoEntity(forProperty("nmo"), inits.get("nmo")) : null;
        this.user = inits.isInitialized("user") ? new QUserEntity(forProperty("user"), inits.get("user")) : null;
    }

}

