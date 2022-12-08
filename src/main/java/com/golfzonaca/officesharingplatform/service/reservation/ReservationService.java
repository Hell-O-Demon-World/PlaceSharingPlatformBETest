package com.golfzonaca.officesharingplatform.service.reservation;

import com.golfzonaca.officesharingplatform.domain.Place;
import com.golfzonaca.officesharingplatform.domain.User;
import com.golfzonaca.officesharingplatform.web.reservation.dto.process.ProcessReservationData;
import com.golfzonaca.officesharingplatform.web.reservation.dto.response.ReservationResponseData;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public interface ReservationService {
    Boolean existReservationThatDay(Long placeId, String roomType, LocalDate date);
    
    List<ReservationResponseData> getReservationResponseData(Place place, String selectedType, String inputDate) throws IOException;

    List<Integer> findAvailableTimes(Long placeId, String selectedType, LocalDate date, LocalTime startTime);

    Map<String, Object> saveReservation(User user, Place place, ProcessReservationData data);

}
