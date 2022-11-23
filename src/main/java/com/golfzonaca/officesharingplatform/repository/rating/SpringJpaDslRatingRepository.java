package com.golfzonaca.officesharingplatform.repository.rating;

import com.golfzonaca.officesharingplatform.domain.Rating;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
    public Rating update(Rating rating) {
        return jpaRepository.save(rating);
    }

    @Override
    public void delete(Rating rating) {
        jpaRepository.delete(rating);
    }
}
