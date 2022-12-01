package com.golfzonaca.officesharingplatform.repository.reservation;

import com.golfzonaca.officesharingplatform.domain.Place;
import com.golfzonaca.officesharingplatform.domain.Reservation;
import com.golfzonaca.officesharingplatform.domain.Room;
import com.golfzonaca.officesharingplatform.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
@RequiredArgsConstructor
public class CustomReservationRepository implements ReservationRepository {
    private final SpringDataJpaReservationRepository jpaReservationRepository;
    private final QueryReservationRepository queryReservationRepository;

    @Override
    public Reservation save(Reservation reservation) {
        return jpaReservationRepository.save(reservation);
    }

    @Override
    public List<Reservation> findAllByUserId(Long userId) {
        return jpaReservationRepository.findAllById(Collections.singleton(userId));
    }

    @Override
    public List<Reservation> findResByPlaceIdAndRoomKindId(long roomTypeId, LocalDate resStartDate, LocalDate resEndDate) {
        return queryReservationRepository.findAll(ReservationSearchCond.builder()
                .roomKindId(roomTypeId)
                .resStartDate(resStartDate)
                .resEndDate(resEndDate)
                .build());
    }

    @Override
    public void deleteById(Long reservationId) {
        jpaReservationRepository.deleteById(reservationId);
    }

    @Override
    public List<Reservation> findAll(ReservationSearchCond cond) {
        return queryReservationRepository.findAll(cond);
    }

    @Override
    public boolean findInResValid(User user, Place place, LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime) {
        return queryReservationRepository.findInResValid(user, place, startDate, startTime, endDate, endTime).isEmpty();
    }

    @Override
    public List<Reservation> findResByRoomKindAndDateTime(String selectedType, LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime) {
        return queryReservationRepository.findResByRoomKindAndDateTime(selectedType, startDate, startTime, endDate, endTime);
    }

    @Override
    public List<Reservation> findAllByPlaceIdAndRoomTypeAndDate(Long placeId, String roomType, LocalDate date) {
        return queryReservationRepository.findAllByPlaceIdAndRoomTypeAndDate(placeId, roomType, date);
    }

    @Override
    public Optional<Reservation> findByPlaceIdAndRoomTypeAndDate(Long placeId, String roomType, LocalDate date) {
        return queryReservationRepository.findFirst(placeId, roomType, date);
    }

    @Override
    public Optional<Reservation> findById(Long reservationId) {
        return jpaReservationRepository.findById(reservationId);
    }

    @Override
    public List<Reservation> findAllLimit(ReservationSearchCond cond, Integer maxNum) {
        return queryReservationRepository.findAllLimit(cond, maxNum);
    }

}
