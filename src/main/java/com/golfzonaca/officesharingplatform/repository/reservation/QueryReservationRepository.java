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

import static com.golfzonaca.officesharingplatform.domain.QReservation.reservation;


@Transactional
@Repository
public class QueryReservationRepository {
    private final JPAQueryFactory query;

    public QueryReservationRepository(EntityManager em) {
        this.query = new JPAQueryFactory(em);
    }

    List<Reservation> findAll(ReservationSearchCond cond) {
        Long userId = cond.getUserId();
        Long roomId = cond.getRoomId();
        Long roomKindId = cond.getRoomKindId();
        Long placeId = cond.getPlaceId();
        LocalTime resStartTime = cond.getResStartTime();
        LocalDate resStartDate = cond.getResStartDate();
        LocalTime resEndTime = cond.getResEndTime();
        LocalDate resEndDate = cond.getResEndDate();

        return query
                .select(reservation)
                .from(reservation)
                .where(eqUserId(userId), eqRoomId(roomId), eqRoomKindId(roomKindId), eqPlaceId(placeId)
                        , eqResStartTime(resStartTime), eqResStartDate(resStartDate)
                        , eqResEndTime(resEndTime), eqResEndDate(resEndDate))
                .fetch();
    }

    private BooleanExpression eqUserId(Long userId) {
        if (reservation != null) {
            return reservation.user.id.eq(userId);
        }
        return null;
    }

    private BooleanExpression eqRoomId(Long roomId) {
        if (reservation != null) {
            return reservation.room.id.eq(roomId);
        }
        return null;
    }

    private BooleanExpression eqRoomKindId(Long roomKindId) {
        if (reservation != null) {
            return reservation.room.roomKind.id.eq(roomKindId);
        }
        return null;
    }

    private BooleanExpression eqPlaceId(Long placeId) {
        if (reservation != null) {
            return reservation.place.id.eq(placeId);
        }
        return null;
    }

    private BooleanExpression eqResStartTime(LocalTime resStartTime) {
        if (reservation != null) {
            return reservation.resStartTime.eq(resStartTime);
        }
        return null;
    }

    private BooleanExpression eqResStartDate(LocalDate resStartDate) {
        if (reservation != null) {
            return reservation.resStartDate.eq(resStartDate);
        }
        return null;
    }

    private BooleanExpression eqResEndTime(LocalTime resEndTime) {
        if (reservation != null) {
            return reservation.resEndTime.eq(resEndTime);
        }
        return null;
    }

    private BooleanExpression eqResEndDate(LocalDate resEndDate) {
        if (reservation != null) {
            return reservation.resEndDate.eq(resEndDate);
        }
        return null;
    }

}
