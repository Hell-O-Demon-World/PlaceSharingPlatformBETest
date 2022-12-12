package com.golfzonaca.officesharingplatform.repository.rating;

import com.golfzonaca.officesharingplatform.domain.Place;
import com.golfzonaca.officesharingplatform.domain.Rating;
import com.golfzonaca.officesharingplatform.domain.User;
import com.golfzonaca.officesharingplatform.web.rating.dto.RatingUpdateData;

import java.util.List;

public interface RatingRepository {
    Rating save(Rating rating);

    Rating findById(long ratingId);

    void update(Rating rating, RatingUpdateData updateData);

    void delete(Rating rating);

    List<Rating> findAllByUser(User user, Integer page);

    List<Rating> findAllByPlace(Place place, long page);
}
