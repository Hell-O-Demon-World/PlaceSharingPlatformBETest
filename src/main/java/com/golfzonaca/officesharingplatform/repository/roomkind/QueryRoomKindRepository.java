package com.golfzonaca.officesharingplatform.repository.roomkind;

import com.golfzonaca.officesharingplatform.domain.RoomKind;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import java.util.Optional;

import static com.golfzonaca.officesharingplatform.domain.QRoomKind.roomKind;

@Repository
@Transactional
public class QueryRoomKindRepository {
    private final JPAQueryFactory query;

    public QueryRoomKindRepository(EntityManager em) {
        this.query = new JPAQueryFactory(em);
    }

    public Optional<RoomKind> validRoomType(String roomType) {
        return Optional.ofNullable(query
                .selectFrom(roomKind)
                .where(likeRoomType(roomType))
                .fetchFirst());
    }

    private BooleanExpression likeRoomType(String roomType) {
        if (StringUtils.hasText(roomType)) {
            return roomKind.roomType.like(roomType);
        }
        return null;
    }
}
