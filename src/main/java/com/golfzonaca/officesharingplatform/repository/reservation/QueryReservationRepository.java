package com.golfzonaca.officesharingplatform.repository.reservation;

import com.golfzonaca.officesharingplatform.domain.*;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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
                .selectFrom(reservation)
                .where(eqUserId(userId), eqRoomId(roomId), eqRoomKindId(roomKindId), eqPlaceId(placeId)
                        , eqResStartTime(resStartTime), eqResStartDate(resStartDate)
                        , eqResEndTime(resEndTime), eqResEndDate(resEndDate))
                .fetch();
    }

    public List<Reservation> findInResValid(User user, Place place, LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime) {
        return query
                .selectFrom(reservation)
                .where(userEquals(user), PlaceEquals(place), startDateEquals(startDate), endDateEquals(endDate), startTimeLoe(startTime).and(endTimeGt(startTime)).or(startTimeGt(startTime).and(startTimeLt(endTime))))
                .fetch();
    }

    public List<Reservation> findResByRoomKindAndDateTime(String selectedType, LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime) {
        return query
                .selectFrom(reservation)
                .where(roomTypeLike(selectedType), startDateEquals(startDate), startTimeLoe(startTime), endDateEquals(endDate), endTimeGoe(endTime))
                .fetch();
    }

    public List<Reservation> findAllByPlaceIdAndRoomTypeAndDate(Long placeId, String roomType, LocalDate date) {
        Optional<Long> optionalPlaceId = Optional.ofNullable(placeId);
        Optional<String> optionalRoomType = Optional.ofNullable(roomType);
        Optional<LocalDate> optionalLocalDate = Optional.ofNullable(date);
        return query
                .select(reservation)
                .from(reservation)
                .where(eqPlaceId(optionalPlaceId), eqRoomType(optionalRoomType), eqResStartDate(optionalLocalDate))
                .fetch();
    }

    public Optional<Reservation> findByUserAndRoom(User user, Room room) {
        if (user != null && room != null) {
            return Optional.ofNullable(query
                    .selectFrom(reservation)
                    .where(userEquals(user), roomEquals(room))
                    .fetchOne());
        }
        return null;
    }

    private BooleanExpression userEquals(User user) {
        if (user != null) {
            return reservation.user.eq(user);
        }
        return null;
    }

    private BooleanExpression PlaceEquals(Place place) {
        if (place != null) {
            return reservation.place.eq(place);
        }
        return null;
    }

    private BooleanExpression startDateEquals(LocalDate date) {
        if (date != null) {
            return reservation.resStartDate.eq(date);
        }
        return null;
    }

    private BooleanExpression endDateEquals(LocalDate date) {
        if (date != null) {
            return reservation.resEndDate.eq(date);
        }
        return null;
    }

    private BooleanExpression startTimeLoe(LocalTime time) {
        if (time != null) {
            return reservation.resStartTime.loe(time);
        }
        return null;
    }

    private BooleanExpression endTimeGt(LocalTime time) {
        if (time != null) {
            return reservation.resEndTime.gt(time);
        }
        return null;
    }

    private BooleanExpression startTimeGt(LocalTime time) {
        if (time != null) {
            return reservation.resStartTime.gt(time);
        }
        return null;
    }

    private BooleanExpression startTimeLt(LocalTime time) {
        if (time != null) {
            return reservation.resStartTime.lt(time);
        }
        return null;
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

    private BooleanExpression roomTypeLike(String selectedType) {
        if (StringUtils.hasText(selectedType)) {
            return reservation.room.roomKind.roomType.like(selectedType);
        }
        return null;
    }

    private BooleanExpression endTimeGoe(LocalTime endTime) {
        if (endTime != null) {
            return reservation.resEndTime.goe(endTime);
        }
        return null;
    }

    private BooleanExpression eqRoomType(Optional<String> optionalRoomType) {
        if (optionalRoomType.isPresent()) {
            return reservation.room.roomKind.roomType.eq(optionalRoomType.get());
        }
        return null;
    }


    private BooleanExpression roomEquals(Room room) {
        if (room != null) {
            return QRoom.room.eq(room);
        }
        return null;
    }
}
