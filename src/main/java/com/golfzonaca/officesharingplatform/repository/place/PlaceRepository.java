package com.golfzonaca.officesharingplatform.repository.place;

import com.golfzonaca.officesharingplatform.domain.Place;
import com.golfzonaca.officesharingplatform.web.main.dto.request.RequestSearchData;
import com.querydsl.core.Tuple;

import java.util.List;

public interface PlaceRepository {
    List<Place> findAllPlaces();

    Place findById(long id);

    List<Place> findPlaces(RequestSearchData requestSearchData);

    List<Place> filterPlaces(String day, String startTime, String endTime, String city, String subCity, String type);

    String findOpenDayById(Long id);

    Tuple findStartAndEndTimeById(Long id);
}
