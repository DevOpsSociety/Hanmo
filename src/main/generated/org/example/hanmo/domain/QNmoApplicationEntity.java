package org.example.hanmo.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QNmoApplicationEntity is a Querydsl query type for NmoApplicationEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QNmoApplicationEntity extends EntityPathBase<NmoApplicationEntity> {

    private static final long serialVersionUID = 1278765864L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QNmoApplicationEntity nmoApplicationEntity = new QNmoApplicationEntity("nmoApplicationEntity");

    public final QBaseTimeEntity _super = new QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createDate = _super.createDate;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QNmoEntity nmo;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final QUserEntity user;

    public QNmoApplicationEntity(String variable) {
        this(NmoApplicationEntity.class, forVariable(variable), INITS);
    }

    public QNmoApplicationEntity(Path<? extends NmoApplicationEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QNmoApplicationEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QNmoApplicationEntity(PathMetadata metadata, PathInits inits) {
        this(NmoApplicationEntity.class, metadata, inits);
    }

    public QNmoApplicationEntity(Class<? extends NmoApplicationEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.nmo = inits.isInitialized("nmo") ? new QNmoEntity(forProperty("nmo"), inits.get("nmo")) : null;
        this.user = inits.isInitialized("user") ? new QUserEntity(forProperty("user"), inits.get("user")) : null;
    }

}

