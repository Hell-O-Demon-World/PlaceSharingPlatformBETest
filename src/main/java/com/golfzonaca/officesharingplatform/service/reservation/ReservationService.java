package com.golfzonaca.officesharingplatform.service.reservation;

import com.golfzonaca.officesharingplatform.domain.Place;
import com.golfzonaca.officesharingplatform.domain.Reservation;
import com.golfzonaca.officesharingplatform.domain.User;
import com.golfzonaca.officesharingplatform.web.reservation.dto.process.ProcessReservationData;
import com.golfzonaca.officesharingplatform.web.reservation.form.SelectedTypeAndDayForm;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ReservationService {

    Map<String, String> findRoom(long placeId);

    List<Integer> getReservationTimeList(Long placeId, SelectedTypeAndDayForm selectedTypeAndDayForm);

    List<Reservation> findResByPlaceIdAndRoomKindId(long roomTypeId, LocalDate resStartDate, LocalDate resEndDate);

    Map<String, String> validation(Map<String, String> response, User user, Place place, ProcessReservationData data);

    Map<String, String> saveReservation(Map<String, String> response, User user, Place place, ProcessReservationData data);
}
