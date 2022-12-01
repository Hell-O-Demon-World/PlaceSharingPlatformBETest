package com.golfzonaca.officesharingplatform.service.reservation;

import com.golfzonaca.officesharingplatform.domain.Place;
import com.golfzonaca.officesharingplatform.domain.User;
import com.golfzonaca.officesharingplatform.web.reservation.dto.process.ProcessReservationData;
import com.golfzonaca.officesharingplatform.web.reservation.dto.response.ReservationResponseData;
import com.golfzonaca.officesharingplatform.web.reservation.dto.response.ReservationResponseTypeForm;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public interface ReservationService {

    ReservationResponseTypeForm findRoom(long placeId);

    List<ReservationResponseData> getReservationResponseData(Long placeId, String selectedType, String inputDate);

    List<Integer> findAvailableTimes(Long placeId, String selectedType, LocalDate date, LocalTime startTime);

    Map<String, String> validation(Map<String, String> response, User user, Place place, ProcessReservationData data);

    Map<String, Object> saveReservation(User user, Place place, ProcessReservationData data);
}
