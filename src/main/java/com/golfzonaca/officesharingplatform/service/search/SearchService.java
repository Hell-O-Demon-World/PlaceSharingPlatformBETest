package com.golfzonaca.officesharingplatform.service.search;

import com.golfzonaca.officesharingplatform.controller.main.dto.request.RequestSearchData;
import com.golfzonaca.officesharingplatform.domain.Place;

import java.util.List;

public interface SearchService {
    List<Place> searchPlaces(RequestSearchData requestSearchData);

    List<Place> filterPlaces(String day, String startTime, String endTime, String city, String subCity, String typeCategory);
}
