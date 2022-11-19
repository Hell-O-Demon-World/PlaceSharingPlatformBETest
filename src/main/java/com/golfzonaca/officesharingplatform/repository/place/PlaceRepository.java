package com.golfzonaca.officesharingplatform.repository.place;

import com.golfzonaca.officesharingplatform.domain.Place;
import com.golfzonaca.officesharingplatform.web.search.form.SearchRequestData;

import java.util.List;

public interface PlaceRepository {
    Place findById(long id);

    List<Place> findPlaces(SearchRequestData searchRequestData);
}
