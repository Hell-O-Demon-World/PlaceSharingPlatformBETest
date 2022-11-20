package com.golfzonaca.officesharingplatform.repository.place;

import com.golfzonaca.officesharingplatform.domain.Place;
import com.golfzonaca.officesharingplatform.domain.type.RoomType;
import com.golfzonaca.officesharingplatform.web.search.form.SearchRequestData;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

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

    public List<Place> findPlaces(Optional<SearchRequestData> searchRequestData) {
        String searchWord = null;
        String day = null;
        LocalTime startTime = null;
        LocalTime endTime = null;
        RoomType roomType = null;
        
        if (searchRequestData.isPresent()) {
            searchWord = searchRequestData.get().getSearchWord();
            day = searchRequestData.get().getDay();
            startTime = searchRequestData.get().getStartTime();
            endTime = searchRequestData.get().getEndTime();
            roomType = searchRequestData.get().getRoomType();
        }

        return query
                .selectFrom(place)
                .join(place.rooms, room)
                .join(room.roomKind, roomKind)
                .where(likeName(searchWord), likeAddress(searchWord), likeDay(day), beforeStartTime(startTime), afterEndTime(endTime), likeRoomType(roomType))
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

    private BooleanExpression likeRoomType(RoomType roomType) {
        if (StringUtils.hasText(roomType.toString())) {
            return roomKind.roomType.contains(String.valueOf(roomType));
        }
        return null;
    }
}