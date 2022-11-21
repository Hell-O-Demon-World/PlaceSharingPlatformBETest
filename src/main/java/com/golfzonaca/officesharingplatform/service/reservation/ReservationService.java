package com.golfzonaca.officesharingplatform.service.reservation;

import com.golfzonaca.officesharingplatform.domain.Place;
import com.golfzonaca.officesharingplatform.domain.Reservation;
import com.golfzonaca.officesharingplatform.domain.User;
import com.golfzonaca.officesharingplatform.web.reservation.form.ResRequestData;
import com.golfzonaca.officesharingplatform.web.reservation.form.SelectedDateTimeForm;
import com.google.gson.JsonObject;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ReservationService {

    JsonObject findRoom(long placeId);

    List<Integer> getReservationTimeList(Long placeId, SelectedDateTimeForm selectedDateTimeForm);

    List<Reservation> findResByPlaceIdAndRoomKindId(long placeId, long roomTypeId, LocalDate resStartDate, LocalDate resEndDate);

    Map<String, String> reservation(Map<String, String> errorMap, User user, Place place, ResRequestData resRequestData);

}
