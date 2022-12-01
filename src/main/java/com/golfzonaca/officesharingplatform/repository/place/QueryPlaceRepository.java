package com.golfzonaca.officesharingplatform.repository.place;

import com.golfzonaca.officesharingplatform.domain.Place;
import com.golfzonaca.officesharingplatform.web.formatter.TimeFormatter;
import com.golfzonaca.officesharingplatform.web.main.dto.request.RequestFilterData;
import com.golfzonaca.officesharingplatform.web.main.dto.request.RequestSearchData;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import java.time.LocalTime;
import java.util.List;

import static com.golfzonaca.officesharingplatform.domain.QPlace.place;
import static com.golfzonaca.officesharingplatform.domain.QRoom.room;
import static com.golfzonaca.officesharingplatform.domain.QRoomKind.roomKind;

@Repository
@Transactional
public class QueryPlaceRepository {
    private final JPAQueryFactory query;

    public QueryPlaceRepository(EntityManager em) {
        this.query = new JPAQueryFactory(em);
    }

    public List<Place> findPlaces(RequestSearchData requestSearchData) {

        String searchWord = requestSearchData.getSearchWord();

        return query
                .selectFrom(place)
                .innerJoin(place.rooms, room)
                .innerJoin(room.roomKind, roomKind)
                .where(likeName(searchWord).or(likeAddress(searchWord)))
                .groupBy(place)
                .fetch();
    }

    public List<Place> filterPlaces(RequestFilterData requestFilterData) {
        String day = null;
        if (StringUtils.hasText(requestFilterData.getDay())) {
            day = TimeFormatter.toDayOfTheWeek(TimeFormatter.toLocalDate(requestFilterData.getDay()));
        }
        LocalTime startTime = null;
        if (StringUtils.hasText(requestFilterData.getStartTime())) {
            startTime = TimeFormatter.toLocalTime(requestFilterData.getStartTime());
        }
        LocalTime endTime = null;
        if (StringUtils.hasText(requestFilterData.getEndTime())) {
            endTime = TimeFormatter.toLocalTime(requestFilterData.getEndTime());
        }
        String city = requestFilterData.getCity();
        String subCity = requestFilterData.getSubCity();
        String roomType = requestFilterData.getType();


        return query
                .selectFrom(place)
                .join(place.rooms, room)
                .join(room.roomKind, roomKind)
                .where(likeAddress(city), likeAddress(subCity), likeDay(day), beforeStartTime(startTime), afterEndTime(endTime), likeRoomType(roomType))
                .groupBy(place)
                .fetch();
    }

    public String findOpenDayById(Long id) {
        return query
                .select(place.openDays)
                .from(place)
                .where(place.id.eq(id))
                .fetchFirst();
    }

    public Tuple findStartAndEndTimeById(Long id) {
        return query.select(place.placeStart, place.placeEnd)
                .from(place)
                .where(place.id.eq(id))
                .fetchFirst();
    }

    private BooleanExpression likeName(String name) {
        if (StringUtils.hasText(name)) {
            return place.placeName.contains(name);
        }
        return null;
    }

    private BooleanExpression likeAddress(String address) {
        if (StringUtils.hasText(address)) {
            return place.address.address.contains(address);
        }
        return null;
    }

    private BooleanExpression likeDay(String day) {
        if (StringUtils.hasText(day)) {
            return place.openDays.contains(day);
        }
        return null;
    }

    private BooleanExpression beforeStartTime(LocalTime startTime) {
        if (startTime != null) {
            if (StringUtils.hasText(startTime.toString())) {
                return place.placeStart.loe(startTime);
            }
        }
        return null;
    }

    private BooleanExpression afterEndTime(LocalTime endTime) {
        if (endTime != null) {
            if (StringUtils.hasText(endTime.toString())) {
                return place.placeEnd.goe(endTime);
            }
        }
        return null;
    }

    private BooleanExpression likeRoomType(String roomType) {
        if (StringUtils.hasText(roomType)) {
            return room.roomKind.roomType.contains(roomType);
        }
        return null;
    }

}