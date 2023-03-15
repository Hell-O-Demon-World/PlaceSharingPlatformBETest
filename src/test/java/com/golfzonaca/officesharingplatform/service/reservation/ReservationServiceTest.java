package com.golfzonaca.officesharingplatform.service.reservation;

import com.golfzonaca.officesharingplatform.controller.reservation.dto.process.ProcessReservationData;
import com.golfzonaca.officesharingplatform.domain.Place;
import com.golfzonaca.officesharingplatform.domain.Reservation;
import com.golfzonaca.officesharingplatform.domain.User;
import com.golfzonaca.officesharingplatform.exception.NonExistedPlaceException;
import com.golfzonaca.officesharingplatform.repository.place.PlaceRepository;
import com.golfzonaca.officesharingplatform.repository.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;

@Slf4j
@Transactional
@SpringBootTest
class ReservationServiceTest {

    @Autowired
    ReservationService reservationService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PlaceRepository placeRepository;

    @Test
    void 예약로직4번() {
        //given
        User user = userRepository.findById(1L);
        Place place = placeRepository.findById(5L).orElseThrow(NonExistedPlaceException::new);
        ProcessReservationData data = new ProcessReservationData("DESK", LocalDate.of(2022, 12, 23), LocalDate.of(2022, 12, 23), LocalTime.of(13, 0), LocalTime.of(15, 0));
        //when
        Reservation reservation = reservationService.saveReservation(user, place, data);
        //then
        log.info("reservation={}", reservation);
    }
}