package com.golfzonaca.officesharingplatform.repository.room;

import com.golfzonaca.officesharingplatform.domain.Room;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static com.golfzonaca.officesharingplatform.domain.QRoom.room;


@Transactional
@Repository
public class QueryRoomRepository {
    private final JPAQueryFactory query;

    public QueryRoomRepository(EntityManager em) {
        this.query = new JPAQueryFactory(em);
    }

    List<Room> findAll(RoomSearchCond cond) {
        Long roomKindId = cond.getRoomKindId();
        Long placeId = cond.getPlaceId();
        Integer totalNum = cond.getTotalNum();

        return query
                .select(room)
                .from(room)
                .where(eqRoomKindId(roomKindId), eqPlaceId(placeId), eqTotalNum(totalNum))
                .fetch();
    }
    private BooleanExpression eqRoomKindId(Long roomKindId) {
        if (room != null) {
            return room.roomKind.id.eq(roomKindId);
        }
        return null;
    }

    private BooleanExpression eqPlaceId(Long placeId) {
        if (room != null) {
            return room.place.id.eq(placeId);
        }
        return null;
    }
    private BooleanExpression eqTotalNum(Integer totalNum) {
        if (room != null) {
            return room.totalNum.eq(totalNum);
        }
        return null;
    }

}
