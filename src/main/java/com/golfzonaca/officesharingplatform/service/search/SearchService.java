package com.golfzonaca.officesharingplatform.service.search;

import com.golfzonaca.officesharingplatform.domain.Place;
import com.golfzonaca.officesharingplatform.web.search.dto.RequestFilterData;
import com.golfzonaca.officesharingplatform.web.search.dto.RequestSearchData;

import java.util.List;

public interface SearchService {
    List<Place> findPlaces(RequestSearchData requestSearchData);

    List<Place> filterPlaces(RequestFilterData requestFilterData);
}
