package com.golfzonaca.officesharingplatform.service.rating;

import com.golfzonaca.officesharingplatform.domain.Rating;
import com.golfzonaca.officesharingplatform.web.rating.dto.RatingSaveData;
import com.golfzonaca.officesharingplatform.web.rating.dto.RatingUpdateData;

public interface RatingService {

    void save(Long userId, Long reservationId, RatingSaveData ratingSaveData);

    Rating findById(long ratingId);

    void update(Long reservationId, long ratingId, RatingUpdateData updateData);

    void delete(Long userId, long ratingId);
}
