package com.golfzonaca.officesharingplatform.repository.place;

import com.golfzonaca.officesharingplatform.domain.Place;
import com.golfzonaca.officesharingplatform.domain.type.RoomType;
import com.golfzonaca.officesharingplatform.web.main.dto.request.RequestSearchData;
import com.querydsl.core.Tuple;

import java.time.LocalTime;
import java.util.List;

public interface PlaceRepository {
    List<Place> findAllPlaces();

    Place findById(long id);

    List<Place> findPlaces(RequestSearchData requestSearchData);

    List<Place> filterPlaces(String day, LocalTime startTime, LocalTime endTime, String city, String subCity, List<RoomType> roomTypeList);

    String findOpenDayById(Long id);

    Tuple findStartAndEndTimeById(Long id);
}
