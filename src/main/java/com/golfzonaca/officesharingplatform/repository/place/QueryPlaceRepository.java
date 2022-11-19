package com.golfzonaca.officesharingplatform.repository.place;

import com.golfzonaca.officesharingplatform.domain.*;
import com.golfzonaca.officesharingplatform.domain.type.RoomType;
import com.golfzonaca.officesharingplatform.web.search.form.SearchRequestData;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;

import static com.golfzonaca.officesharingplatform.domain.QPlace.place;

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
        RoomType roomType = searchRequestData.getRoomType();

        return query
                .selectFrom(place)
                .join(place.rooms, QRoom.room)
                .join(QRoom.room.roomKind, QRoomKind.roomKind)
                .where(likeName(searchWord), likeAddress(searchWord), likeDay(day), beforeStartTime(startTime), afterEndTime(endTime), likeRoomType(roomType))
                .fetch();
    }

    private BooleanExpression likeName(String searchWord) {
        if (StringUtils.hasText(searchWord)) {
            return place.placeName.like("%" + searchWord + "%");
        }
        return null;
    }

    private BooleanExpression likeAddress(String searchWord) {
        if (StringUtils.hasText(searchWord)) {
            return place.address.address.like("%" + searchWord + "%");
        }
        return null;
    }

    private BooleanExpression likeDay(String day) {
        if (StringUtils.hasText(day)) {
            return place.openDays.like("%" + day + "%");
        }
        return null;
    }

    private BooleanExpression beforeStartTime(LocalTime startTime) {
        if (StringUtils.hasText(startTime.toString())) {
            return place.placeStart.before(startTime);
        }
        return null;
    }

    private BooleanExpression afterEndTime(LocalTime endTime) {
        if (StringUtils.hasText(endTime.toString())) {
            return place.placeEnd.after(endTime);
        }
        return null;
    }

    private BooleanExpression likeRoomType(Place place, RoomType roomType) {
        if (StringUtils.hasText(roomType.toString())) {
            if (place.getRooms() != null) {
                roomTypeInfo(place)
            }
        }
        return null;
    }

    private String roomTypeInfo(QPlace place) {
        if (place.rooms != null) {
            HashSet<String> roomTypeInfo = new HashSet<>();
            for (int i = 0; i < place.rooms.size(); i++) {

            }
            for (Room room : place.rooms.get(i)) {
                roomTypeInfo.add(room.getRoomKind().getRoomType());
            }
            return roomTypeInfo.toString();
        }
        return null;
    }
}