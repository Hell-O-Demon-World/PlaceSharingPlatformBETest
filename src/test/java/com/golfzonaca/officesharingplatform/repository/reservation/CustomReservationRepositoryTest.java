package com.golfzonaca.officesharingplatform.repository.reservation;

import com.golfzonaca.officesharingplatform.domain.Reservation;
import com.golfzonaca.officesharingplatform.domain.type.RoomType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
@Transactional
public class CustomReservationRepositoryTest {
    @Autowired
    ReservationRepository reservationRepository;

    @Test
    void findAllByPlaceIdAndRoomTypeAndDate2() {
        LocalDate localDate = LocalDate.of(2022, 12, 19);

        List<Reservation> allByPlaceIdAndRoomTypeAndDate2 = reservationRepository.findAllByPlaceIdAndRoomTypeAndDate2(7L, RoomType.DESK, localDate);
        for (Reservation reservation : allByPlaceIdAndRoomTypeAndDate2) {
            System.out.println("reservation = " + reservation.getId());
        }
    }
}