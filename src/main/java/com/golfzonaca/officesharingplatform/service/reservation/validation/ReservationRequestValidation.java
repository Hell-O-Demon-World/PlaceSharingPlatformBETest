package com.golfzonaca.officesharingplatform.service.reservation.validation;

import com.golfzonaca.officesharingplatform.domain.Place;
import com.golfzonaca.officesharingplatform.domain.Room;
import com.golfzonaca.officesharingplatform.domain.User;
import com.golfzonaca.officesharingplatform.repository.reservation.ReservationRepository;
import com.golfzonaca.officesharingplatform.repository.room.RoomRepository;
import com.golfzonaca.officesharingplatform.repository.roomkind.RoomKindRepository;
import com.golfzonaca.officesharingplatform.web.reservation.form.ResRequestData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ReservationRequestValidation {

    private final RoomRepository roomRepository;
    private final RoomKindRepository roomKindRepository;
    private final ReservationRepository reservationRepository;

    public Map<String, String> validation(Map<String, String> response, User user, Place place, ResRequestData resRequestData) {
        response = validRoomType(response, resRequestData.getSelectedType());
        response = validBusinessDay(response, place, resRequestData.getDate());
        response = validResTimeBetweenPlaceOpeningTime(response, place, resRequestData.getStartTime(), resRequestData.getEndTime());
        response = validStartTimeBeforeEndTime(response, resRequestData.getStartTime(), resRequestData.getEndTime());
        response = validMinUnitOfTime(response, resRequestData.getStartTime(), resRequestData.getEndTime());
        response = validPastnessOfDateTime(response, LocalDateTime.of(resRequestData.getDate(), resRequestData.getStartTime()), LocalDateTime.of(resRequestData.getDate(), resRequestData.getEndTime()));
        response = existRoomTypeInPlace(response, place, resRequestData.getSelectedType());
        response = validDuplicatedResForSameUser(response, user, place, resRequestData.getDate(), resRequestData.getStartTime(), resRequestData.getDate(), resRequestData.getEndTime());
        response = validRestRoomForSelectedPlaceAndDateTime(response, place, resRequestData.getSelectedType(), resRequestData.getDate(), resRequestData.getStartTime(), resRequestData.getDate(), resRequestData.getEndTime());
        return response;
    }

    private Map<String, String> validRoomType(Map<String, String> response, String roomType) {
        boolean existence = roomKindRepository.validRoomType(roomType);
        if (existence) {
            return response;
        }
        response.put("NonexistentRoomTypeError", "선택하신 타입은 존재하지 않습니다.");
        return response;
    }

    private Map<String, String> validBusinessDay(Map<String, String> response, Place place, LocalDate date) {
        boolean businessDay = place.getOpenDays().contains(date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.US));
        if (businessDay) {
            return response;
        }
        response.put("InvalidBusinessDayError", "선택하신 요일은 휴무일입니다.");
        return response;
    }

    private Map<String, String> validResTimeBetweenPlaceOpeningTime(Map<String, String> response, Place place, LocalTime resStartTime, LocalTime resEndTime) {
        if (place.getPlaceStart().getHour() <= resStartTime.getHour() && resStartTime.getHour() < place.getPlaceEnd().getHour()) {
            if (place.getPlaceStart().getHour() < resEndTime.getHour() && resEndTime.getHour() + 1 <= place.getPlaceEnd().getHour()) {
                return response;
            } else {
                response.put("InvalidResEndTimeError", "선택하신 예약 종료 시간은 영업 시간이 아닙니다.");
                return response;
            }
        } else {
            response.put("InvalidResStartTimeError", "선택하신 예약 시작 시간은 영업 시간이 아닙니다.");
            return response;
        }
    }


    private Map<String, String> validStartTimeBeforeEndTime(Map<String, String> response, LocalTime startTime, LocalTime endTime) {
        if (startTime.isBefore(endTime) || startTime.equals(endTime)) {
            return response;
        }
        response.put("StartTimeAfterEndTimeError", "시작 시각이 종료 시각 이후입니다.");
        return response;
    }

    private Map<String, String> validMinUnitOfTime(Map<String, String> response, LocalTime startTime, LocalTime endTime) {
        if (startTime.plusHours(1).isBefore(endTime) || startTime.plusHours(1).equals(endTime) || startTime.isAfter(endTime)) {
            return response;
        }
        response.put("InvalidResTimeError", "최소 1시간 이상 예약 가능합니다.");
        return response;
    }

    private Map<String, String> validPastnessOfDateTime(Map<String, String> response, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        if (startDateTime.isAfter(LocalDateTime.now()) && endDateTime.isAfter(LocalDateTime.now())) {
            return response;
        }
        response.put("PastDateTimeError", "예약일시가 현재보다 과거입니다.");
        return response;
    }

    private Map<String, String> existRoomTypeInPlace(Map<String, String> response, Place place, String roomType) {
        List<Room> rooms = place.getRooms();
        for (Room room : rooms) {
            if (room.getRoomKind().getRoomType().equals(roomType)) {
                return response;
            }
        }
        response.put("NonexistentRoomTypeInPlaceError", "선택하신 타입은 해당 공간에 존재하지 않습니다.");
        return response;
    }

    private Map<String, String> validDuplicatedResForSameUser(Map<String, String> response, User user, Place place, LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime) {
        if (reservationRepository.findInResValid(user, place, startDate, startTime, endDate, endTime)) {
            return response;
        }
        response.put("DuplicatedResForUserError", "선택하신 시간과 공간에 대한 예약 내역이 존재합니다.");
        return response;
    }

    private Map<String, String> validRestRoomForSelectedPlaceAndDateTime(Map<String, String> response, Place place, String selectedType, LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime) {
        if (roomRepository.findRoomByPlaceAndRoomKind(place, selectedType).size() > reservationRepository.findResByRoomKindAndDateTime(selectedType, startDate, startTime, endDate, endTime).size()) {
            return response;
        }
        String message = "";
        if (selectedType.contains("DESK")) {
            message = selectedType.replace("DESK", "데스크");
        } else if (selectedType.contains("MEETINGROOM")) {
            message = selectedType.replace("MEETINGROOM", "") + "인 회의실";
        } else if (selectedType.contains("OFFICE")) {
            message = selectedType.replace("OFFICE", "") + "평 사무실";
        }
        response.put("NonexistentRestRoomForSelectedPlaceAndDateTime", "선택하신 시간과 공간에 대해 빈 " + message + "이(가) 없습니다.");
        return response;
    }
}
