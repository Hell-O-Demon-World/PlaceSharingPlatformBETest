package com.golfzonaca.officesharingplatform.repository.reservation;


import com.golfzonaca.officesharingplatform.domain.Place;
import com.golfzonaca.officesharingplatform.domain.Reservation;
import com.golfzonaca.officesharingplatform.domain.User;
import com.golfzonaca.officesharingplatform.domain.type.RoomType;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository {
    Reservation save(Reservation reservation);

    List<Reservation> findAllByUserId(Long userId);

    Optional<Reservation> findInDateByPlaceIdAndRoomTypeAndDate(Long placeId, String roomType, LocalTime date);

    List<Reservation> findResByPlaceIdAndRoomKindId(long roomTypeId, LocalDate resStartDate, LocalDate resEndDate);

    void deleteById(Long reservationId);

    List<Reservation> findAll(ReservationSearchCond cond);

    boolean findInResValid(User user, Place place, LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime);

    List<Reservation> findResByRoomKindAndDateTime(String selectedType, LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime);

    List<Reservation> findAllByPlaceIdAndRoomTypeAndDate(Long placeId, RoomType roomType, LocalDate date);

    Optional<Reservation> findByPlaceIdAndRoomTypeAndDate(Long placeId, RoomType roomType, LocalDate date);

    Reservation findById(Long reservationId);

    List<Reservation> findAllLimit(ReservationSearchCond cond, Integer maxNum);

}
