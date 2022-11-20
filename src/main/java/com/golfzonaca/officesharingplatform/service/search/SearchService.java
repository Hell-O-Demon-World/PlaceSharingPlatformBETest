package com.golfzonaca.officesharingplatform.service.search;

import com.golfzonaca.officesharingplatform.domain.Place;
import com.golfzonaca.officesharingplatform.web.search.dto.SearchRequestData;

import java.util.List;

public interface SearchService {
    List<Place> findPlaces(SearchRequestData searchRequestData);
}
