package org.example.hanmo.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMatchingPreferencesEntity is a Querydsl query type for MatchingPreferencesEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMatchingPreferencesEntity extends EntityPathBase<MatchingPreferencesEntity> {

    private static final long serialVersionUID = 1165558553L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMatchingPreferencesEntity matchingPreferencesEntity = new QMatchingPreferencesEntity("matchingPreferencesEntity");

    public final QBaseTimeEntity _super = new QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createDate = _super.createDate;

    public final NumberPath<Long> matchingPreferencesId = createNumber("matchingPreferencesId", Long.class);

    public final NumberPath<Integer> preferredAgeMax = createNumber("preferredAgeMax", Integer.class);

    public final NumberPath<Integer> preferredAgeMin = createNumber("preferredAgeMin", Integer.class);

    public final EnumPath<org.example.hanmo.domain.enums.PreferredGender> preferredGender = createEnum("preferredGender", org.example.hanmo.domain.enums.PreferredGender.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final QUserEntity user;

    public QMatchingPreferencesEntity(String variable) {
        this(MatchingPreferencesEntity.class, forVariable(variable), INITS);
    }

    public QMatchingPreferencesEntity(Path<? extends MatchingPreferencesEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMatchingPreferencesEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMatchingPreferencesEntity(PathMetadata metadata, PathInits inits) {
        this(MatchingPreferencesEntity.class, metadata, inits);
    }

    public QMatchingPreferencesEntity(Class<? extends MatchingPreferencesEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new QUserEntity(forProperty("user"), inits.get("user")) : null;
    }

}

