package com.golfzonaca.officesharingplatform.repository.rating;

import com.golfzonaca.officesharingplatform.domain.Rating;

public interface RatingRepository {
    Rating save(Rating rating);

    Rating update(Rating rating);

    void delete(Rating rating);
}
