package com.golfzonaca.officesharingplatform.repository.reservation;

import com.golfzonaca.officesharingplatform.domain.Reservation;
import com.golfzonaca.officesharingplatform.repository.mybatis.ReservationMapper;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
public class MyBatisReservationRepository {

    private final ReservationMapper reservationMapper;

    public List<Reservation> findAllByPlaceIdAndRoomKindIdAndDate(Long placeId, Long roomKindId, LocalDate reservationDate) {
        return reservationMapper.findAllByPlaceIdAndRoomKindIdAndDate(placeId, roomKindId, reservationDate);

    }

    public List<Reservation> findAllByUserId(Long userId) {
        return reservationMapper.findAllByUserId(userId);
    }

    public void save(Reservation reservation) {
        reservationMapper.save(reservation);
    }

    public List<Reservation> findResByPlaceIdAndRoomKindId(long placeId, long roomTypeId, LocalDate resStartDate, LocalDate resEndDate) {
        return reservationMapper.findResByPlaceIdAndRoomKindId(placeId, roomTypeId, resStartDate, resEndDate);
    }

    public List<Integer> findRoomTypeByPlaceId(long placeId) {
        return reservationMapper.findRoomTypeByPlaceId(placeId);
    }

    public void deleteById(Long reservationId) {
        reservationMapper.deleteById(reservationId);
    }

}
