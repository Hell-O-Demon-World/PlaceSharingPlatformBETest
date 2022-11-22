package com.golfzonaca.officesharingplatform.service.reservation.validation;

import com.golfzonaca.officesharingplatform.domain.Place;
import com.golfzonaca.officesharingplatform.domain.Reservation;
import com.golfzonaca.officesharingplatform.domain.Room;
import com.golfzonaca.officesharingplatform.web.reservation.form.ResRequestData;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class ReservationProcessValidation {
    public Room selectAvailableRoomForReservation(Place place, ResRequestData resRequestData) {
        Room room = null;
        for (Room candidate : place.getRooms()) {
            if (candidate.getRoomKind().getRoomType().equals(resRequestData.getSelectedType())) {
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
                for (int resRequestTime = resRequestData.getStartTime().getHour(); resRequestTime < resRequestData.getEndTime().getHour() + 1; resRequestTime++) {
                    if (reservationTimetable.containsKey(resRequestTime)) {
                        if (resRequestTime == resRequestData.getEndTime().getHour()) {
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
