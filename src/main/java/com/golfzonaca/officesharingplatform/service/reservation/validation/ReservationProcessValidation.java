package com.golfzonaca.officesharingplatform.service.reservation.validation;

import com.golfzonaca.officesharingplatform.domain.Place;
import com.golfzonaca.officesharingplatform.domain.Reservation;
import com.golfzonaca.officesharingplatform.domain.Room;
import com.golfzonaca.officesharingplatform.web.reservation.dto.process.ProcessReservationData;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class ReservationProcessValidation {
    public Room selectAvailableRoomForReservation(Place place, ProcessReservationData data) {
        Room room = null;
        for (Room candidate : place.getRooms()) {
            if (candidate.getRoomKind().getRoomType().equals(data.getSelectedType())) {
                Map<Integer, Integer> reservationTimetable = new LinkedHashMap<>();
                for (int openingPlaceTime = place.getPlaceStart().getHour(); openingPlaceTime < place.getPlaceEnd().getHour(); openingPlaceTime++) {
                    reservationTimetable.put(openingPlaceTime, openingPlaceTime);
                }
                if (candidate.getReservationList().size() != 0) {
                    for (Reservation reservation : candidate.getReservationList()) {
                        for (int existingResTime = reservation.getResStartTime().getHour(); existingResTime < reservation.getResEndTime().getHour(); existingResTime++) {
                            reservationTimetable.remove(existingResTime);
                        }
                    }
                } else {
                    room = candidate;
                }
                for (int resRequestTime = data.getStartTime().getHour(); resRequestTime < data.getEndTime().getHour() + 1; resRequestTime++) {
                    if (reservationTimetable.containsKey(resRequestTime)) {
                        if (resRequestTime == data.getEndTime().getHour()) {
                            room = candidate;
                        }
                    } else {
                        break;
                    }
                }
            }
        }
        return room;
    }
}
