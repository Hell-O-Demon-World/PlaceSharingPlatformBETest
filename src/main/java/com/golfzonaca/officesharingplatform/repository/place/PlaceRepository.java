package com.golfzonaca.officesharingplatform.repository.place;

import com.golfzonaca.officesharingplatform.controller.main.dto.request.RequestSearchData;
import com.golfzonaca.officesharingplatform.domain.Place;
import com.golfzonaca.officesharingplatform.repository.place.dto.FilterData;
import com.querydsl.core.Tuple;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlaceRepository {
    List<Place> searchPlaces(RequestSearchData requestSearchData);

    List<Place> filterPlaces(FilterData filterData);

    Optional<String> findOpenDayById(Long id);

    Tuple findStartAndEndTimeById(Long id);

    List<Place> findAll();

    Optional<Place> findById(long placeId);
}
