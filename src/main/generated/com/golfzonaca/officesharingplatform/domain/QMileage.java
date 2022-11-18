package com.golfzonaca.officesharingplatform.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QMileage is a Querydsl query type for Mileage
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMileage extends EntityPathBase<Mileage> {

    private static final long serialVersionUID = -1901107052L;

    public static final QMileage mileage = new QMileage("mileage");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> point = createNumber("point", Long.class);

    public QMileage(String variable) {
        super(Mileage.class, forVariable(variable));
    }

    public QMileage(Path<? extends Mileage> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMileage(PathMetadata metadata) {
        super(Mileage.class, metadata);
    }

}

