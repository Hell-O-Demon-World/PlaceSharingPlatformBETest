package com.golfzonaca.officesharingplatform.repository.reservation;

import com.golfzonaca.officesharingplatform.domain.Reservation;
import com.golfzonaca.officesharingplatform.repository.mybatis.ReservationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MyBatisReservationRepository implements ReservationRepository {

    private final ReservationMapper reservationMapper;

    @Override
    public List<Reservation> findAllByPlaceIdAndRoomKindIdAndDate(Long placeId, Long roomKindId, LocalDate reservationDate) {
        return reservationMapper.findAllByPlaceIdAndRoomKindIdAndDate(placeId, roomKindId, reservationDate);

    }

    @Override
    public List<Reservation> findAllByUserId(Long userId) {
        return reservationMapper.findAllByUserId(userId);
    }

    @Override
    public void save(Reservation reservation) {
        reservationMapper.save(reservation);
    }

    @Override
    public List<Reservation> findResByPlaceIdAndRoomKindId(long placeId, long roomTypeId, LocalDate resStartDate, LocalDate resEndDate) {
        return reservationMapper.findResByPlaceIdAndRoomKindId(placeId, roomTypeId, resStartDate, resEndDate);
    }

    @Override
    public List<Integer> findRoomTypeByPlaceId(long placeId) {
        return reservationMapper.findRoomTypeByPlaceId(placeId);
    }

    @Override
    public void deleteById(Long reservationId) {
        reservationMapper.deleteById(reservationId);
    }

    @Override
    public Reservation findById(Long reservationId) {
        return reservationMapper.findById(reservationId);
    }

    @Override
    public Reservation findByUserIdAndRoomId(long userId, long roomId) {
        return reservationMapper.findByUserIdAndRoomId(userId, roomId);
    }
}
