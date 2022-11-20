package com.golfzonaca.officesharingplatform.service.search;

import com.golfzonaca.officesharingplatform.domain.Place;
import com.golfzonaca.officesharingplatform.web.search.form.SearchRequestData;

import java.util.List;
import java.util.Optional;

public interface SearchService {
    List<Place> findPlaces(Optional<SearchRequestData> searchRequestData);
}
