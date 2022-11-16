package com.golfzonaca.officesharingplatform.repository.reservation;

import com.golfzonaca.officesharingplatform.domain.Reservation;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository {

    List<Reservation> findAllByPlaceIdAndRoomKindIdAndDate(Long placeId, Long roomKindId,LocalDate reservationDate);
    
    List<Reservation> findAllByUserId(Long userId);

    void save(Reservation reservation);
    
    List<Reservation> findResByPlaceIdAndRoomKindId(long placeId, long roomTypeId, LocalDate resStartDate, LocalDate resEndDate);

    List<Integer> findRoomTypeByPlaceId(long placeId);

    void deleteById(Long reservationId);
}
