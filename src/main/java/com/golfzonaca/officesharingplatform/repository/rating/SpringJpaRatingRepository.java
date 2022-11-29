package com.golfzonaca.officesharingplatform.repository.rating;

import com.golfzonaca.officesharingplatform.domain.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringJpaRatingRepository extends JpaRepository<Rating, Long> {
}
