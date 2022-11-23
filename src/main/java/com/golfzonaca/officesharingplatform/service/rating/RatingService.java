package com.golfzonaca.officesharingplatform.service.rating;

import com.golfzonaca.officesharingplatform.domain.Rating;

public interface RatingService {

    Rating save(Rating rating);

    Rating update(Rating rating);

    void delete(Rating rating);

//    List<Rating> findByPlace(Place place);
}
