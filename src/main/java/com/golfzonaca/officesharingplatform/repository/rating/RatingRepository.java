package com.golfzonaca.officesharingplatform.repository.rating;

import com.golfzonaca.officesharingplatform.domain.Rating;
import com.golfzonaca.officesharingplatform.web.rating.dto.RatingUpdateData;

import java.util.Optional;

public interface RatingRepository {
    Rating save(Rating rating);

    Optional<Rating> findById(long ratingId);

    void update(Rating rating, RatingUpdateData updateData);

    void delete(Rating rating);
}
