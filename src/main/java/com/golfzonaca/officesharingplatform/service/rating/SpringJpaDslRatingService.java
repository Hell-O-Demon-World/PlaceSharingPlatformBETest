package com.golfzonaca.officesharingplatform.service.rating;

import com.golfzonaca.officesharingplatform.domain.Place;
import com.golfzonaca.officesharingplatform.domain.Rating;
import com.golfzonaca.officesharingplatform.domain.User;
import com.golfzonaca.officesharingplatform.repository.place.PlaceRepository;
import com.golfzonaca.officesharingplatform.repository.ratepoint.RatePointRepository;
import com.golfzonaca.officesharingplatform.repository.rating.RatingRepository;
import com.golfzonaca.officesharingplatform.repository.user.UserRepository;
import com.golfzonaca.officesharingplatform.web.formatter.TimeFormatter;
import com.golfzonaca.officesharingplatform.web.rating.dto.RatingSaveData;
import com.golfzonaca.officesharingplatform.web.rating.dto.RatingUpdateData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class SpringJpaDslRatingService implements RatingService {
    private final RatingRepository ratingRepository;
    private final PlaceRepository placeRepository;
    private final UserRepository userRepository;
    private final RatePointRepository ratePointRepository;

    @Override
    public void save(Long userId, long placeId, RatingSaveData ratingSaveData) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원입니다."));
        Place place = placeRepository.findById(placeId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 공간입니다."));
        Rating rating = new Rating(place, Float.parseFloat(ratingSaveData.getRatingScore()), ratingSaveData.getRatingReview(), user, TimeFormatter.toLocalDateTime(ratingSaveData.getRatingTime()));
        Rating rate = ratingRepository.save(rating);
        ratePointRepository.update(place.getRatePoint(), (place.getRatePoint().getRatingPoint() + rate.getRatingScore()) / 2);
    }

    @Override
    public Rating findById(long ratingId) {
        return ratingRepository.findById(ratingId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 후기입니다."));
    }

    @Override
    public void update(Long userId, long ratingId, RatingUpdateData updateData) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원입니다."));
        Rating rating = ratingRepository.findById(ratingId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 후기입니다."));
        if (rating.getRatingWriter() == user) {
            ratingRepository.update(rating, updateData);
            ratePointRepository.update(rating.getPlace().getRatePoint(), (rating.getPlace().getRatePoint().getRatingPoint() + Float.parseFloat(updateData.getRatingScore()) / 2));
        }
    }

    @Override
    public void delete(Long userId, long ratingId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원입니다."));
        Rating rating = ratingRepository.findById(ratingId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 후기입니다."));
        if (rating.getRatingWriter() == user) {
            ratingRepository.delete(rating);
            ratePointRepository.update(rating.getPlace().getRatePoint(), rating.getPlace().getRatePoint().getRatingPoint() * 2 - rating.getRatingScore());
        }
    }

}
