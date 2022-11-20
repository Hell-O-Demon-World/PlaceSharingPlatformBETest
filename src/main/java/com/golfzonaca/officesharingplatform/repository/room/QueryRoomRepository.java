package com.golfzonaca.officesharingplatform.repository.room;

import com.golfzonaca.officesharingplatform.domain.Place;
import com.golfzonaca.officesharingplatform.domain.Room;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static com.golfzonaca.officesharingplatform.domain.QRoom.room;


@Transactional
@Repository
public class QueryRoomRepository {
    private final JPAQueryFactory query;

    public QueryRoomRepository(EntityManager em) {
        this.query = new JPAQueryFactory(em);
    }

    List<Long> findIdAll(RoomSearchCond cond) {
        Optional<Long> roomKindId = Optional.ofNullable(cond.getRoomKindId());
        Optional<Long> placeId = Optional.ofNullable(cond.getPlaceId());
        Optional<Integer> totalNum = Optional.ofNullable(cond.getTotalNum());
        return query
                .select(room.id)
                .from(room)
                .where(eqRoomKindId(roomKindId), eqPlaceId(placeId), eqTotalNum(totalNum))
                .fetch();
    }

    List<Room> findAll(RoomSearchCond cond) {
        Optional<Long> roomKindId = Optional.ofNullable(cond.getRoomKindId());
        Optional<Long> placeId = Optional.ofNullable(cond.getPlaceId());
        Optional<Integer> totalNum = Optional.ofNullable(cond.getTotalNum());

        return query
                .select(room)
                .from(room)
                .where(eqRoomKindId(roomKindId), eqPlaceId(placeId), eqTotalNum(totalNum))
                .fetch();
    }

    public List<Room> findRoomByPlaceAndRoomKind(Place place, String selectedType) {
        return query
                .selectFrom(room)
                .where(placeEq(place), roomTypeLike(selectedType))
                .fetch();
    }

    private BooleanExpression eqRoomKindId(Optional<Long> roomKindId) {
        if (roomKindId.isPresent()) {
            return room.roomKind.id.eq(roomKindId.get());
        }
        return null;
    }

    private BooleanExpression eqPlaceId(Optional<Long> placeId) {
        if (placeId.isPresent()) {
            return room.place.id.eq(placeId.get());
        }
        return null;
    }

    private BooleanExpression eqTotalNum(Optional<Integer> totalNum) {
        if (totalNum.isPresent()) {
            return room.totalNum.eq(totalNum.get());
        }
        return null;
    }

    private BooleanExpression placeEq(Place place) {
        if (place != null) {
            return room.place.eq(place);
        }
        return null;
    }

    private BooleanExpression roomTypeLike(String selectedType) {
        if (StringUtils.hasText(selectedType)) {
            return room.roomKind.roomType.like(selectedType);
        }
        return null;
    }
}
