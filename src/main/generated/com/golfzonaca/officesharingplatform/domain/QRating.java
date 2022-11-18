package com.golfzonaca.officesharingplatform.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QRating is a Querydsl query type for Rating
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRating extends EntityPathBase<Rating> {

    private static final long serialVersionUID = -1864988525L;

    public static final QRating rating = new QRating("rating");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath ratingReview = createString("ratingReview");

    public final NumberPath<Integer> ratingScore = createNumber("ratingScore", Integer.class);

    public final DateTimePath<java.time.LocalDateTime> ratingTime = createDateTime("ratingTime", java.time.LocalDateTime.class);

    public final StringPath ratingWriter = createString("ratingWriter");

    public QRating(String variable) {
        super(Rating.class, forVariable(variable));
    }

    public QRating(Path<? extends Rating> path) {
        super(path.getType(), path.getMetadata());
    }

    public QRating(PathMetadata metadata) {
        super(Rating.class, metadata);
    }

}

