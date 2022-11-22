package com.golfzonaca.officesharingplatform.repository.place;

import com.golfzonaca.officesharingplatform.domain.Place;
import com.golfzonaca.officesharingplatform.web.search.dto.RequestFilterData;
import com.golfzonaca.officesharingplatform.web.search.dto.RequestSearchData;

import java.util.List;
import java.util.Optional;

public interface PlaceRepository {
    Optional<Place> findById(long id);

    List<Place> findPlaces(RequestSearchData requestSearchData);

    List<Place> filterPlaces(RequestFilterData requestFilterData);
}
