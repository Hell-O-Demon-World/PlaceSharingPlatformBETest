package com.golfzonaca.officesharingplatform.repository.reservation;

import com.golfzonaca.officesharingplatform.domain.Place;
import com.golfzonaca.officesharingplatform.domain.Reservation;
import com.golfzonaca.officesharingplatform.domain.User;
import com.golfzonaca.officesharingplatform.domain.type.RoomType;
import com.golfzonaca.officesharingplatform.exception.NonExistedReservationException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
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
    public List<Reservation> findAllByUserWithPagination(User user, Integer page, LocalDate date) {
        return queryReservationRepository.findAllByUserWithPagination(user, page, date);
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
    public List<Reservation> findResByRoomKindAndDateTime(RoomType selectedType, LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime) {
        return queryReservationRepository.findResByRoomKindAndDateTime(selectedType, startDate, startTime, endDate, endTime);
    }

    @Cacheable(cacheNames = "resDataByPlaceAndTypeAndDate", sync = true, key = "#placeId+'&'+#roomType+'&'+#date")
    @Override
    public List<Reservation> findAllByPlaceIdAndRoomTypeAndDate(Long placeId, RoomType roomType, LocalDate date) {
        return queryReservationRepository.findAllByPlaceIdAndRoomTypeAndDate(placeId, roomType, date);
    }

    @Override
    public Optional<Reservation> findByPlaceIdAndRoomTypeAndDate(Long placeId, RoomType roomType, LocalDate date) {
        return queryReservationRepository.findFirstByPlaceIdAndRoomTypeAndDate(placeId, roomType, date);
    }

    @Override
    public Optional<Reservation> findInDateByPlaceIdAndRoomTypeAndDate(Long placeId, RoomType roomType, LocalTime time) {
        return queryReservationRepository.findInTimeByPlaceAndRoomTypeAndDate(placeId, roomType, time);
    }

    @Override
    public Reservation findById(Long reservationId) {
        return jpaReservationRepository.findById(reservationId).orElseThrow(() -> new NonExistedReservationException("존재하지 않는 예약입니다."));
    }

    @Override
    public List<Reservation> findAllLimit(ReservationSearchCond cond, Integer maxNum) {
        return queryReservationRepository.findAllLimit(cond, maxNum);
    }

    @Override
    public List<Reservation> findByUserAndDate(User user, LocalDate date) {
        return queryReservationRepository.findByUserAndDate(user, date);
    }

}
