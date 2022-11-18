package com.golfzonaca.officesharingplatform.repository.place;

import com.golfzonaca.officesharingplatform.domain.Place;

public interface PlaceRepository {
    Place findById(long id);
}
