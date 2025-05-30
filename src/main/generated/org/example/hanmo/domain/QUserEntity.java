package org.example.hanmo.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserEntity is a Querydsl query type for UserEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserEntity extends EntityPathBase<UserEntity> {

    private static final long serialVersionUID = 1684517971L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserEntity userEntity = new QUserEntity("userEntity");

    public final QBaseTimeEntity _super = new QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createDate = _super.createDate;

    public final EnumPath<org.example.hanmo.domain.enums.Department> department = createEnum("department", org.example.hanmo.domain.enums.Department.class);

    public final EnumPath<org.example.hanmo.domain.enums.Gender> gender = createEnum("gender", org.example.hanmo.domain.enums.Gender.class);

    public final EnumPath<org.example.hanmo.domain.enums.GenderMatchingType> genderMatchingType = createEnum("genderMatchingType", org.example.hanmo.domain.enums.GenderMatchingType.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath instagramId = createString("instagramId");

    public final StringPath loginId = createString("loginId");

    public final StringPath loginPw = createString("loginPw");

    public final QMatchingGroupsEntity matchingGroup;

    public final EnumPath<org.example.hanmo.domain.enums.MatchingType> matchingType = createEnum("matchingType", org.example.hanmo.domain.enums.MatchingType.class);

    public final EnumPath<org.example.hanmo.domain.enums.Mbti> mbti = createEnum("mbti", org.example.hanmo.domain.enums.Mbti.class);

    public final StringPath name = createString("name");

    public final StringPath nickname = createString("nickname");

    public final BooleanPath nicknameChanged = createBoolean("nicknameChanged");

    public final ListPath<NmoEntity, QNmoEntity> nmo = this.<NmoEntity, QNmoEntity>createList("nmo", NmoEntity.class, QNmoEntity.class, PathInits.DIRECT2);

    public final ListPath<NmoApplicationEntity, QNmoApplicationEntity> nmoApplication = this.<NmoApplicationEntity, QNmoApplicationEntity>createList("nmoApplication", NmoApplicationEntity.class, QNmoApplicationEntity.class, PathInits.DIRECT2);

    public final StringPath phoneNumber = createString("phoneNumber");

    public final ListPath<PostEntity, QPostEntity> post = this.<PostEntity, QPostEntity>createList("post", PostEntity.class, QPostEntity.class, PathInits.DIRECT2);

    public final StringPath studentNumber = createString("studentNumber");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final EnumPath<org.example.hanmo.domain.enums.UserRole> userRole = createEnum("userRole", org.example.hanmo.domain.enums.UserRole.class);

    public final EnumPath<org.example.hanmo.domain.enums.UserStatus> userStatus = createEnum("userStatus", org.example.hanmo.domain.enums.UserStatus.class);

    public final EnumPath<org.example.hanmo.domain.enums.WithdrawalStatus> withdrawalStatus = createEnum("withdrawalStatus", org.example.hanmo.domain.enums.WithdrawalStatus.class);

    public final DateTimePath<java.time.LocalDateTime> withdrawalTimestamp = createDateTime("withdrawalTimestamp", java.time.LocalDateTime.class);

    public QUserEntity(String variable) {
        this(UserEntity.class, forVariable(variable), INITS);
    }

    public QUserEntity(Path<? extends UserEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUserEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUserEntity(PathMetadata metadata, PathInits inits) {
        this(UserEntity.class, metadata, inits);
    }

    public QUserEntity(Class<? extends UserEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.matchingGroup = inits.isInitialized("matchingGroup") ? new QMatchingGroupsEntity(forProperty("matchingGroup")) : null;
    }

}

