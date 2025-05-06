package org.example.hanmo.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMatchingGroupsEntity is a Querydsl query type for MatchingGroupsEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMatchingGroupsEntity extends EntityPathBase<MatchingGroupsEntity> {

    private static final long serialVersionUID = 1561440761L;

    public static final QMatchingGroupsEntity matchingGroupsEntity = new QMatchingGroupsEntity("matchingGroupsEntity");

    public final QBaseTimeEntity _super = new QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createDate = _super.createDate;

    public final NumberPath<Integer> femaleCount = createNumber("femaleCount", Integer.class);

    public final EnumPath<org.example.hanmo.domain.enums.GenderMatchingType> genderMatchingType = createEnum("genderMatchingType", org.example.hanmo.domain.enums.GenderMatchingType.class);

    public final EnumPath<org.example.hanmo.domain.enums.GroupStatus> groupStatus = createEnum("groupStatus", org.example.hanmo.domain.enums.GroupStatus.class);

    public final BooleanPath isSameDepartment = createBoolean("isSameDepartment");

    public final NumberPath<Integer> maleCount = createNumber("maleCount", Integer.class);

    public final NumberPath<Long> matchingGroupId = createNumber("matchingGroupId", Long.class);

    public final EnumPath<org.example.hanmo.domain.enums.MatchingType> matchingType = createEnum("matchingType", org.example.hanmo.domain.enums.MatchingType.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final ListPath<UserEntity, QUserEntity> users = this.<UserEntity, QUserEntity>createList("users", UserEntity.class, QUserEntity.class, PathInits.DIRECT2);

    public QMatchingGroupsEntity(String variable) {
        super(MatchingGroupsEntity.class, forVariable(variable));
    }

    public QMatchingGroupsEntity(Path<? extends MatchingGroupsEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMatchingGroupsEntity(PathMetadata metadata) {
        super(MatchingGroupsEntity.class, metadata);
    }

}

