package com.golfzonaca.officesharingplatform.repository.reservation;


import com.golfzonaca.officesharingplatform.domain.Place;
import com.golfzonaca.officesharingplatform.domain.Reservation;
import com.golfzonaca.officesharingplatform.domain.Room;
import com.golfzonaca.officesharingplatform.domain.User;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository {
    Reservation save(Reservation reservation);

    List<Reservation> findAllByPlaceIdAndRoomKindIdAndDate(Long placeId, Long roomKindId, LocalDate reservationDate);

    List<Reservation> findAllByUserId(Long userId);

    List<Reservation> findResByPlaceIdAndRoomKindId(long placeId, long roomTypeId, LocalDate resStartDate, LocalDate resEndDate);

    List<Integer> findRoomTypeByPlaceId(long placeId);

    void deleteById(Long reservationId);

    List<Reservation> findAll(ReservationSearchCond cond);

    boolean findInResValid(User user, Place place, LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime);

    List<Reservation> findResByRoomKindAndDateTime(String selectedType, LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime);

    List<Reservation> findAllByPlaceIdAndRoomTypeAndDate(Long placeId, String roomType, LocalDate date);

    Optional<Reservation> findById(Long reservationId);

    Optional<Reservation> findByUserAndRoom(User user, Room room);

    List<Reservation> findAllLimit(ReservationSearchCond cond, Integer maxNum);
}
