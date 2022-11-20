package com.golfzonaca.officesharingplatform.repository.place;

import com.golfzonaca.officesharingplatform.domain.Place;

import java.util.List;
import java.util.Optional;

public interface PlaceRepository {
    Optional<Place> findById(long id);

    List<Place> findByPlaceNameLike(String searchWord);
}
