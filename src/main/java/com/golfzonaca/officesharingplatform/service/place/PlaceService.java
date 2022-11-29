package com.golfzonaca.officesharingplatform.service.place;

import com.golfzonaca.officesharingplatform.domain.Place;
import com.golfzonaca.officesharingplatform.service.place.dto.response.PlaceDto;

import java.util.List;
import java.util.Map;

public interface PlaceService {
    List<Place> findAllPlaces();

    Place findById(long placeId);

    boolean isOpenDay(Long id, String day);

    boolean isExistPlace(long placeId);

    boolean isOpenToday(Long id, String startTime);

    Map<Integer, PlaceDto> processingMainPlaceData();
}
