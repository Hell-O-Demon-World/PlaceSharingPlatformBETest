package com.golfzonaca.officesharingplatform.domain;

import com.golfzonaca.officesharingplatform.domain.type.RoomType;
import com.golfzonaca.officesharingplatform.repository.room.RoomRepository;
import com.golfzonaca.officesharingplatform.repository.user.UserRepository;
import com.golfzonaca.officesharingplatform.service.mileage.MileageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class ReservationTest {

    @Autowired
    EntityManager em;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoomRepository roomRepository;

    @Autowired
    MileageService mileageService;

    @BeforeEach
    public void create() {
        String username = "memberA";
        String email = "abc@test.com";
        String password = "password";
        String phoneNumber = "01012341234";
        String job = "학생";
        String userPlace = "";
        Mileage mileage = mileageService.join();

        User user = User.joinUser(username, email, password, phoneNumber, job, userPlace, mileage);
        em.persist(user);

        Address companyAddress = new Address("서울", "00000", 0.0, 0.0);
        em.persist(companyAddress);

        Company company = new Company("company1234", "company1234", "companyA", "0200000000", "012301230123", "Kim", companyAddress);
        em.persist(company);

        RatePoint ratePoint = new RatePoint(0.0F);
        em.persist(ratePoint);

        Address placeAddress = new Address("서울시 종로구", "12345", 1.0, 1.0);
        em.persist(placeAddress);

        Place place = new Place(company, ratePoint, "placeA", "Welcome", "Mon, Tue", LocalTime.of(11, 0), LocalTime.of(23, 0), "Wifi", placeAddress);
        em.persist(place);

        Room room = new Room(RoomKind.desk(), place);
        em.persist(room);

        RoomStatus roomStatus = new RoomStatus(room, true);
        em.persist(roomStatus);
    }

    @Test
    public void createReservation() throws Exception {
        // given
        User user = userRepository.findAll().get(0);
        Room room = roomRepository.findAll().get(0);
        // when
        Reservation reservation = Reservation.createReservation(user, room, LocalDate.now().plusDays(1), LocalTime.of(11, 0), LocalDate.now().plusDays(1), LocalTime.of(14, 0));
        em.persist(reservation);
        em.flush();
        em.clear();

        Reservation findReservation = em.createQuery("select r from Reservation r", Reservation.class)
                .getSingleResult();
        // then
        assertThat(findReservation.getUser().getUsername()).isEqualTo("memberA");
        assertThat(findReservation.getUser().getEmail()).isEqualTo("abc@test.com");
        assertThat(findReservation.getUser().getPassword()).isEqualTo("password");
        assertThat(findReservation.getUser().getPhoneNumber()).isEqualTo("01012341234");
        assertThat(findReservation.getUser().getJob()).isEqualTo("학생");
        assertThat(findReservation.getUser().getUserPlace()).isBlank();

        assertThat(findReservation.getRoom().getRoomKind().getRoomType()).isEqualTo(RoomType.DESK);

        assertThat(findReservation.getResStartDate()).isEqualTo(LocalDate.now().plusDays(1));
        assertThat(findReservation.getResStartTime()).isEqualTo(LocalTime.of(11, 0));
        assertThat(findReservation.getResEndDate()).isEqualTo(LocalDate.now().plusDays(1));
        assertThat(findReservation.getResEndTime()).isEqualTo(LocalTime.of(14, 0));

    }
}