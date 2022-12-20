package com.golfzonaca.officesharingplatform.web.mypage.validation;

import com.golfzonaca.officesharingplatform.repository.rating.RatingRepository;
import com.golfzonaca.officesharingplatform.repository.reservation.ReservationRepository;
import com.golfzonaca.officesharingplatform.repository.room.RoomRepository;
import com.golfzonaca.officesharingplatform.repository.roomkind.RoomKindRepository;
import com.golfzonaca.officesharingplatform.repository.user.UserRepository;
import com.golfzonaca.officesharingplatform.web.validation.RequestValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

@Component
@RequiredArgsConstructor
public class MypageRequestValidation {
    private final RequestValidation requestValidation;

    public void validationUser(long userId) {
        requestValidation.validUser(userId);
    }

    public void validationMypageDetail(Long userId, long reservationId) {
        requestValidation.validUser(userId);
        requestValidation.validUserAndReservation(userId, reservationId);
    }
    public void validationReservation(Long userId, long reservationId) {
        requestValidation.validUser(userId);
        requestValidation.validReservation(reservationId);
        requestValidation.validUserAndReservation(userId, reservationId);
    }
    public void validationReviewRating(Long ratingId) {
        requestValidation.validRating(ratingId);
    }

    public void validationUserAndBindingResult(Long userId, BindingResult bindingResult) {
        requestValidation.validUser(userId);
        requestValidation.bindingResultCheck(bindingResult);
    }
}
