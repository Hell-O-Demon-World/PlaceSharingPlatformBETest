package com.golfzonaca.officesharingplatform.repository.place;

import com.golfzonaca.officesharingplatform.domain.Place;
import com.golfzonaca.officesharingplatform.web.search.dto.request.RequestFilterData;
import com.golfzonaca.officesharingplatform.web.search.dto.request.RequestSearchData;
import com.querydsl.core.Tuple;

import java.util.List;
import java.util.Optional;

public interface PlaceRepository {
    List<Place> findAllPlaces();

    Optional<Place> findById(long id);

    List<Place> findPlaces(RequestSearchData requestSearchData);

    List<Place> filterPlaces(RequestFilterData requestFilterData);

    String findOpenDayById(Long id);

    Tuple findStartAndEndTimeById(Long id);
}
