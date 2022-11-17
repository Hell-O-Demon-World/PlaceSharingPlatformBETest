package com.golfzonaca.officesharingplatform.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QRatePoint is a Querydsl query type for RatePoint
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRatePoint extends EntityPathBase<RatePoint> {

    private static final long serialVersionUID = -318086694L;

    public static final QRatePoint ratePoint = new QRatePoint("ratePoint");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Float> ratingPoint = createNumber("ratingPoint", Float.class);

    public QRatePoint(String variable) {
        super(RatePoint.class, forVariable(variable));
    }

    public QRatePoint(Path<? extends RatePoint> path) {
        super(path.getType(), path.getMetadata());
    }

    public QRatePoint(PathMetadata metadata) {
        super(RatePoint.class, metadata);
    }

}

