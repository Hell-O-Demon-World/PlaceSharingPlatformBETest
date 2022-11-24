package com.golfzonaca.officesharingplatform.repository.rating;

import com.golfzonaca.officesharingplatform.domain.Rating;
import com.golfzonaca.officesharingplatform.web.rating.dto.RatingUpdateData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
@RequiredArgsConstructor
public class SpringJpaDslRatingRepository implements RatingRepository {
    private final SpringJpaRatingRepository jpaRepository;

    @Override
    public Rating save(Rating rating) {
        return jpaRepository.save(rating);
    }

    @Override
    public Optional<Rating> findById(long ratingId) {
        return jpaRepository.findById(ratingId);
    }

    @Override
    public void update(Rating rating, RatingUpdateData updateData) {
        rating.UpdateRating(Float.parseFloat(updateData.getRatingScore()), updateData.getRatingReview());
    }

    @Override
    public void delete(Rating rating) {
        jpaRepository.delete(rating);
    }
}
