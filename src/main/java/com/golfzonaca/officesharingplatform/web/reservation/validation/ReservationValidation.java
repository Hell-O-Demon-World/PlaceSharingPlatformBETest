package com.golfzonaca.officesharingplatform.web.reservation.validation;

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
public class ReservationValidation {

    private final RoomRepository roomRepository;
    private final RoomKindRepository roomKindRepository;
    private final ReservationRepository reservationRepository;

    public Map<String, String> validation(Map<String, String> errorMap, User user, Place place, ResRequestData resRequestData) {
        errorMap = validRoomType(errorMap, resRequestData.getSelectedType());
        errorMap = validOpenDays(errorMap, place, resRequestData.getDate());
        errorMap = validStartTimeBeforeEndTime(errorMap, resRequestData.getStartTime(), resRequestData.getEndTime());
        errorMap = validMinUnitOfTime(errorMap, resRequestData.getStartTime(), resRequestData.getEndTime());
        errorMap = validPastnessOfDateTime(errorMap, LocalDateTime.of(resRequestData.getDate(), resRequestData.getStartTime()), LocalDateTime.of(resRequestData.getDate(), resRequestData.getEndTime()));
        errorMap = existRoomTypeInPlace(errorMap, place, resRequestData.getSelectedType());
        errorMap = validDuplicatedResForSameUser(errorMap, user, place, resRequestData.getDate(), resRequestData.getStartTime(), resRequestData.getDate(), resRequestData.getEndTime());
        errorMap = validRestRoomForSelectedPlaceAndDateTime(errorMap, user, place, resRequestData.getSelectedType(), resRequestData.getDate(), resRequestData.getStartTime(), resRequestData.getDate(), resRequestData.getEndTime());
        return errorMap;
    }

    public Map<String, String> validRoomType(Map<String, String> errorMap, String roomType) {
        boolean existence = roomKindRepository.validRoomType(roomType);
        if (existence) {
            return errorMap;
        }
        errorMap.put("NonexistentRoomTypeError", "선택하신 타입은 존재하지 않습니다.");
        return errorMap;
    }

    public Map<String, String> validOpenDays(Map<String, String> errorMap, Place place, LocalDate date) {
        boolean open = place.getOpenDays().contains(date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.US));
        if (open) {
            return errorMap;
        }
        errorMap.put("InvalidOpenDaysError", "선택하신 요일은 휴무일입니다.");
        return errorMap;
    }

    private Map<String, String> validStartTimeBeforeEndTime(Map<String, String> errorMap, LocalTime startTime, LocalTime endTime) {
        if (startTime.isBefore(endTime) || startTime.equals(endTime)) {
            return errorMap;
        }
        errorMap.put("StartTimeAfterEndTimeError", "시작 시각이 종료 시각 이후입니다.");
        return errorMap;
    }

    private Map<String, String> validMinUnitOfTime(Map<String, String> errorMap, LocalTime startTime, LocalTime endTime) {
        if (startTime.plusHours(1).isBefore(endTime) || startTime.plusHours(1).equals(endTime) || startTime.isAfter(endTime)) {
            return errorMap;
        }
        errorMap.put("InvalidResTimeError", "최소 1시간 이상 예약 가능합니다.");
        return errorMap;
    }

    private Map<String, String> validPastnessOfDateTime(Map<String, String> errorMap, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        if (startDateTime.isAfter(LocalDateTime.now()) && endDateTime.isAfter(LocalDateTime.now())) {
            return errorMap;
        }
        errorMap.put("PastDateTimeError", "예약일시가 현재보다 과거입니다.");
        return errorMap;
    }

    public Map<String, String> existRoomTypeInPlace(Map<String, String> errorMap, Place place, String roomType) {
        List<Room> rooms = place.getRooms();
        for (Room room : rooms) {
            if (room.getRoomKind().getRoomType().equals(roomType)) {
                return errorMap;
            }
        }
        errorMap.put("NonexistentRoomTypeInPlaceError", "선택하신 타입은 해당 공간에 존재하지 않습니다.");
        return errorMap;
    }

    public Map<String, String> validDuplicatedResForSameUser(Map<String, String> errorMap, User user, Place place, LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime) {
        if (reservationRepository.findInResValid(user, place, startDate, startTime, endDate, endTime)) {
            return errorMap;
        }
        errorMap.put("DuplicatedResForUserError", "선택하신 시간과 공간에 대한 예약 내역이 존재합니다.");
        return errorMap;
    }

    public Map<String, String> validRestRoomForSelectedPlaceAndDateTime(Map<String, String> errorMap, User user, Place place, String selectedType, LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime) {
        if (roomRepository.findRoomByPlaceAndRoomKind(place, selectedType).size() > reservationRepository.findResByRoomKindAndDateTime(selectedType, startDate, startTime, endDate, endTime).size()) {
            return errorMap;
        }
        String message = "";
        if (selectedType.contains("DESK")) {
            message = selectedType.replace("DESK", "데스크");
        } else if (selectedType.contains("MEETINGROOM")) {
            message = selectedType.replace("MEETINGROOM", "") + "인 회의실";
        } else if (selectedType.contains("OFFICE")) {
            message = selectedType.replace("OFFICE", "") + "평 사무실";
        }
        errorMap.put("NonexistentRestRoomForSelectedPlaceAndDateTime", "선택하신 시간과 공간에 대해 빈 " + message + "이(가) 없습니다.");
        return errorMap;
    }
}
