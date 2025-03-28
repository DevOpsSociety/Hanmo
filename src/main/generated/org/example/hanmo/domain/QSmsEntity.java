package org.example.hanmo.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QSmsEntity is a Querydsl query type for SmsEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSmsEntity extends EntityPathBase<SmsEntity> {

    private static final long serialVersionUID = 646380855L;

    public static final QSmsEntity smsEntity = new QSmsEntity("smsEntity");

    public final QBaseTimeEntity _super = new QBaseTimeEntity(this);

    public final StringPath authCode = createString("authCode");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createDate = _super.createDate;

    public final DateTimePath<java.time.LocalDateTime> expiredAt = createDateTime("expiredAt", java.time.LocalDateTime.class);

    public final BooleanPath isVerified = createBoolean("isVerified");

    public final NumberPath<Long> phoneAuthId = createNumber("phoneAuthId", Long.class);

    public final StringPath phoneNumber = createString("phoneNumber");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QSmsEntity(String variable) {
        super(SmsEntity.class, forVariable(variable));
    }

    public QSmsEntity(Path<? extends SmsEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSmsEntity(PathMetadata metadata) {
        super(SmsEntity.class, metadata);
    }

}

