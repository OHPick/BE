package com.team11.shareoffice.post.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPost is a Querydsl query type for Post
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPost extends EntityPathBase<Post> {

    private static final long serialVersionUID = -790227212L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPost post = new QPost("post");

    public final QTimestamped _super = new QTimestamped(this);

    public final QAmenities amenities;

    public final NumberPath<Integer> capacity = createNumber("capacity", Integer.class);

    public final StringPath content = createString("content");

    public final StringPath contentDetails = createString("contentDetails");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isDelete = createBoolean("isDelete");

    public final NumberPath<Integer> likeCount = createNumber("likeCount", Integer.class);

    public final StringPath location = createString("location");

    public final com.team11.shareoffice.member.entity.QMember member;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final QOperatingTime operatingTime;

    public final ListPath<String, StringPath> postImages = this.<String, StringPath>createList("postImages", String.class, StringPath.class, PathInits.DIRECT2);

    public final NumberPath<Integer> price = createNumber("price", Integer.class);

    public final StringPath title = createString("title");

    public QPost(String variable) {
        this(Post.class, forVariable(variable), INITS);
    }

    public QPost(Path<? extends Post> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPost(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPost(PathMetadata metadata, PathInits inits) {
        this(Post.class, metadata, inits);
    }

    public QPost(Class<? extends Post> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.amenities = inits.isInitialized("amenities") ? new QAmenities(forProperty("amenities"), inits.get("amenities")) : null;
        this.member = inits.isInitialized("member") ? new com.team11.shareoffice.member.entity.QMember(forProperty("member")) : null;
        this.operatingTime = inits.isInitialized("operatingTime") ? new QOperatingTime(forProperty("operatingTime"), inits.get("operatingTime")) : null;
    }

}

