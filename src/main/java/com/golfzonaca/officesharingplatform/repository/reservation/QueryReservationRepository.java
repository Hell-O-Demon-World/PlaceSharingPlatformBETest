package com.golfzonaca.officesharingplatform.repository.reservation;

import com.golfzonaca.officesharingplatform.domain.Reservation;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static com.golfzonaca.officesharingplatform.domain.QReservation.reservation;


@Transactional
@Repository
public class QueryReservationRepository {
    private final JPAQueryFactory query;

    public QueryReservationRepository(EntityManager em) {
        this.query = new JPAQueryFactory(em);
    }

    List<Reservation> findAll(ReservationSearchCond cond) {
        Optional<Long> userId = Optional.ofNullable(cond.getUserId());
        Optional<Long> roomId = Optional.ofNullable(cond.getRoomId());
        Optional<Long> roomKindId = Optional.ofNullable(cond.getRoomKindId());
        Optional<Long> placeId = Optional.ofNullable(cond.getPlaceId());
        Optional<LocalTime> resStartTime = Optional.ofNullable(cond.getResStartTime());
        Optional<LocalDate> resStartDate = Optional.ofNullable(cond.getResStartDate());
        Optional<LocalTime> resEndTime = Optional.ofNullable(cond.getResEndTime());
        Optional<LocalDate> resEndDate = Optional.ofNullable(cond.getResEndDate());

        return query
                .select(reservation)
                .from(reservation)
                .where(eqUserId(userId), eqRoomId(roomId), eqRoomKindId(roomKindId), eqPlaceId(placeId)
                        , eqResStartTime(resStartTime), eqResStartDate(resStartDate)
                        , eqResEndTime(resEndTime), eqResEndDate(resEndDate))
                .fetch();
    }

    private BooleanExpression eqUserId(Optional<Long> userId) {
        if (userId.isPresent()) {
            return reservation.user.id.eq(userId.get());
        }
        return null;
    }

    private BooleanExpression eqRoomId(Optional<Long> roomId) {
        if (roomId.isPresent()) {
            return reservation.room.id.eq(roomId.get());
        }
        return null;
    }

    private BooleanExpression eqRoomKindId(Optional<Long> roomKindId) {
        if (roomKindId.isPresent()) {
            return reservation.room.roomKind.id.eq(roomKindId.get());
        }
        return null;
    }

    private BooleanExpression eqPlaceId(Optional<Long> placeId) {
        if (placeId.isPresent()) {
            return reservation.place.id.eq(placeId.get());
        }
        return null;
    }

    private BooleanExpression eqResStartTime(Optional<LocalTime> resStartTime) {
        if (resStartTime.isPresent()) {
            return reservation.resStartTime.eq(resStartTime.get());
        }
        return null;
    }

    private BooleanExpression eqResStartDate(Optional<LocalDate> resStartDate) {
        if (resStartDate.isPresent()) {
            return reservation.resStartDate.eq(resStartDate.get());
        }
        return null;
    }

    private BooleanExpression eqResEndTime(Optional<LocalTime> resEndTime) {
        if (resEndTime.isPresent()) {
            return reservation.resEndTime.eq(resEndTime.get());
        }
        return null;
    }

    private BooleanExpression eqResEndDate(Optional<LocalDate> resEndDate) {
        if (resEndDate.isPresent()) {
            return reservation.resEndDate.eq(resEndDate.get());
        }
        return null;
    }

}
