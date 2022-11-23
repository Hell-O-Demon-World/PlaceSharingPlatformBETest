package com.golfzonaca.officesharingplatform.service.place;

import com.golfzonaca.officesharingplatform.domain.Place;

import java.util.List;

public interface PlaceService {
    List<Place> findAllPlaces();

    Place findById(long placeId);
}
