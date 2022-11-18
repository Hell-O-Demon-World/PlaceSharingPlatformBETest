package com.golfzonaca.officesharingplatform.repository.place;

import com.golfzonaca.officesharingplatform.domain.Place;

import java.util.List;

public interface PlaceRepository {
    Place findById(long id);

    List<Place> findByPlaceNameLike(String searchWord);
}
