package com.golfzonaca.officesharingplatform.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QRoomKind is a Querydsl query type for RoomKind
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRoomKind extends EntityPathBase<RoomKind> {

    private static final long serialVersionUID = -1852949371L;

    public static final QRoomKind roomKind = new QRoomKind("roomKind");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> price = createNumber("price", Integer.class);

    public final StringPath roomType = createString("roomType");

    public QRoomKind(String variable) {
        super(RoomKind.class, forVariable(variable));
    }

    public QRoomKind(Path<? extends RoomKind> path) {
        super(path.getType(), path.getMetadata());
    }

    public QRoomKind(PathMetadata metadata) {
        super(RoomKind.class, metadata);
    }

}

