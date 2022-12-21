package com.golfzonaca.officesharingplatform.repository.reservation;

import com.golfzonaca.officesharingplatform.domain.Place;
import com.golfzonaca.officesharingplatform.domain.User;
import com.golfzonaca.officesharingplatform.repository.place.PlaceRepository;
import com.golfzonaca.officesharingplatform.repository.user.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class QueryReservationRepositoryTest {
    @Autowired
    ReservationRepository reservationRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PlaceRepository placeRepository;

    @Test
    void findInResValid() {
        Long userId = 4L;
        User givenUser = userRepository.findById(4L);
        Long placeId = 3L;
        Place givenPlace = placeRepository.findById(placeId);
        LocalDate startDate = LocalDate.now();
        LocalTime startTime = LocalTime.of(18, 0);
        LocalDate endDate = LocalDate.now();
        LocalTime endTime = LocalTime.of(22, 0);

        boolean inResValid = reservationRepository.findInResValid(givenUser, givenPlace
                , startDate, startTime, endDate, endTime);

        System.out.println("inResValid = " + inResValid);
    }
}