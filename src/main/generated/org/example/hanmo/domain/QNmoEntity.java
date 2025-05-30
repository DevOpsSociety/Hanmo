package org.example.hanmo.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QNmoEntity is a Querydsl query type for NmoEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QNmoEntity extends EntityPathBase<NmoEntity> {

    private static final long serialVersionUID = 1838671150L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QNmoEntity nmoEntity = new QNmoEntity("nmoEntity");

    public final QBaseTimeEntity _super = new QBaseTimeEntity(this);

    public final ListPath<NmoApplicationEntity, QNmoApplicationEntity> application = this.<NmoApplicationEntity, QNmoApplicationEntity>createList("application", NmoApplicationEntity.class, QNmoApplicationEntity.class, PathInits.DIRECT2);

    public final StringPath content = createString("content");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createDate = _super.createDate;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> recruitLimit = createNumber("recruitLimit", Integer.class);

    public final StringPath title = createString("title");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final QUserEntity write;

    public QNmoEntity(String variable) {
        this(NmoEntity.class, forVariable(variable), INITS);
    }

    public QNmoEntity(Path<? extends NmoEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QNmoEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QNmoEntity(PathMetadata metadata, PathInits inits) {
        this(NmoEntity.class, metadata, inits);
    }

    public QNmoEntity(Class<? extends NmoEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.write = inits.isInitialized("write") ? new QUserEntity(forProperty("write"), inits.get("write")) : null;
    }

}

