package com.golfzonaca.officesharingplatform.repository.place;

import com.golfzonaca.officesharingplatform.domain.Place;
import com.golfzonaca.officesharingplatform.web.search.form.SearchRequestData;

import java.util.List;
import java.util.Optional;

public interface PlaceRepository {
    Optional<Place> findById(long id);

    List<Place> findPlaces(Optional<SearchRequestData> searchRequestData);
}
