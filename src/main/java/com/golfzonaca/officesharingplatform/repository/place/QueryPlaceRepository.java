package com.golfzonaca.officesharingplatform.repository.place;

import com.golfzonaca.officesharingplatform.domain.Place;
import com.golfzonaca.officesharingplatform.web.search.dto.SearchRequestData;
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

    public List<Place> findPlaces(SearchRequestData searchRequestData) {

        String searchWord = searchRequestData.getSearchWord();
        String day = searchRequestData.getDay();
        LocalTime startTime = searchRequestData.getStartTime();
        LocalTime endTime = searchRequestData.getEndTime();
        String roomType = searchRequestData.getRoomType();


        return query
                .selectFrom(place)
                .join(place.rooms, room)
                .join(room.roomKind, roomKind)
                .where(likeName(searchWord).or(likeAddress(searchWord)), likeDay(day), beforeStartTime(startTime), afterEndTime(endTime), likeRoomType(roomType))
                .groupBy(place)
                .fetch();
    }

    private BooleanExpression likeName(String searchWord) {
        if (StringUtils.hasText(searchWord)) {
            return place.placeName.contains(searchWord);
        }
        return null;
    }

    private BooleanExpression likeAddress(String searchWord) {
        if (StringUtils.hasText(searchWord)) {
            return place.address.address.contains(searchWord);
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
        if (roomType != null) {
            if (StringUtils.hasText(roomType)) {
                return roomKind.roomType.contains(roomType);
            }
        }
        return null;
    }
}