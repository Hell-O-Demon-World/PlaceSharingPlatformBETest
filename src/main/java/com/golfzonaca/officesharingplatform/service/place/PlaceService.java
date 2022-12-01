package com.golfzonaca.officesharingplatform.service.place;

import com.golfzonaca.officesharingplatform.domain.Place;

import java.util.List;

public interface PlaceService {
    List<Place> findAllPlaces();

    Place findById(long placeId);

    boolean isOpenDay(Long id, String day);

    boolean isExistPlace(long placeId);

    boolean isOpenToday(Long id, String startTime);

    boolean selectedDateValidation(String startDate, String startDate1);
}
