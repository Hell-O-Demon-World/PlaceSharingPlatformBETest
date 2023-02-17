package com.golfzonaca.officesharingplatform.domain;

import com.golfzonaca.officesharingplatform.repository.rating.RatingRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
class RatingTest {

    @Autowired
    RatingRepository ratingRepository;

    @Test
    void createRating() {
    }
}