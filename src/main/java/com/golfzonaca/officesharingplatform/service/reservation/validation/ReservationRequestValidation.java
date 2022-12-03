package com.golfzonaca.officesharingplatform.service.reservation.validation;

import com.golfzonaca.officesharingplatform.domain.Place;
import com.golfzonaca.officesharingplatform.domain.User;
import com.golfzonaca.officesharingplatform.exception.*;
import com.golfzonaca.officesharingplatform.repository.reservation.ReservationRepository;
import com.golfzonaca.officesharingplatform.repository.room.RoomRepository;
import com.golfzonaca.officesharingplatform.web.formatter.TimeFormatter;
import com.golfzonaca.officesharingplatform.web.reservation.dto.process.ProcessReservationData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class ReservationRequestValidation {

    private final RoomRepository roomRepository;
    private final ReservationRepository reservationRepository;

    public void validation(Place place, String roomType, String date) {
        validRealDate(date);
        validRoomType(roomType);
        validBusinessDay(place, TimeFormatter.toLocalDate(date));
    }

    public void validation(Place place, String roomType, String date, String startTime) {
        validRealDate(date);
        validRealTime(Integer.parseInt(startTime));
        validRoomType(roomType);
        validBusinessTime(place, TimeFormatter.toLocalTime(startTime));
        validBusinessDay(place, TimeFormatter.toLocalDate(date));
    }

    public void validation(User user, Place place, ProcessReservationData data) {
        validBusinessDay(place, data.getStartDate());
        validResTimeBetweenPlaceOpeningTime(place, data.getStartTime(), data.getEndTime());
        validStartTimeBeforeEndTime(data.getStartTime(), data.getEndTime());
        validPastOfDateTime(LocalDateTime.of(data.getStartDate(), data.getStartTime()), LocalDateTime.of(data.getStartDate(), data.getEndTime()));
        validDuplicatedResForSameUser(user, place, data.getStartDate(), data.getStartTime(), data.getStartDate(), data.getEndTime());
        validRestRoomForSelectedPlaceAndDateTime(place, data.getSelectedType(), data.getStartDate(), data.getStartTime(), data.getStartDate(), data.getEndTime());
        validSelectedDate(data.getStartDate(), data.getEndDate());
    }

    private void validBusinessTime(Place place, LocalTime time) {
        if (place.getPlaceStart().isAfter(time) || place.getPlaceEnd().isBefore(time)) {
            throw new InvalidTimeException("InvalidTimeException::: 해당 시간은 영업시간에 포함되지 않습니다.");
        }
    }

    private void validRealTime(int time) {
        if (time < 0 || time >= 24) {
            throw new InvalidTimeException("InvalidTimeException::: 유효하지 않은 시간입니다. range = 0 ~ 23");
        }
    }
    private void validRoomType(String roomType) {
        if (roomType.toUpperCase().contains("OFFICE")) {
            throw new NonExistedRoomKindException("roomTypeError::: 지원하지 않는 공간유형 입니다.");
        }
    }

    private void validBusinessDay(Place place, LocalDate date) {
        boolean businessDay = place.getOpenDays().contains(date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.US));
        if (!businessDay) {
            throw new NotBusinessDayException("NotBusinessDayException::: 입력한 날짜는 영업일이 아닙니다.");
        }
    }

    private void validResTimeBetweenPlaceOpeningTime(Place place, LocalTime resStartTime, LocalTime resEndTime) {
        int startTime = resStartTime.getHour();
        int endTime = resEndTime.getHour();
        if (startTime == 0) {
            startTime = 24;
        }
        if (endTime == 0) {
            endTime = 24;
        }
        if (place.getPlaceStart().getHour() <= startTime && startTime < place.getPlaceEnd().getHour()) {
            if (place.getPlaceStart().getHour() >= endTime || endTime > place.getPlaceEnd().getHour()) {
                throw new NotBusinessTimeException("NotBusinessTimeException::: 입력된 시간은 영업 시간이 아닙니다.");
            }
        } else {
            throw new NotBusinessTimeException("NotBusinessTimeException::: 입력된 시간은 영업 시간이 아닙니다.");
        }
    }

    private void validStartTimeBeforeEndTime(LocalTime startTime, LocalTime endTime) {
        if (!(startTime.isBefore(endTime) || startTime.equals(endTime))) {
            throw new NotBusinessTimeException("StartTimeAfterEndTimeError::: 시작 시각이 종료 시각 이후입니다.");
        }
    }

    private void validPastOfDateTime(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        if (!(startDateTime.isAfter(LocalDateTime.now()) && endDateTime.isAfter(LocalDateTime.now()))) {
            throw new NotBusinessDayException("PastDateTimeError::: 예약일시가 현재보다 과거입니다.");
        }
    }

    private void validDuplicatedResForSameUser(User user, Place place, LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime) {
        if (!reservationRepository.findInResValid(user, place, startDate, startTime, endDate, endTime)) {
            throw new DuplicatedReservationException("DuplicatedResForUserError::: 선택하신 시간과 공간에 대한 예약 내역이 존재합니다.");
        }
    }

    private void validRestRoomForSelectedPlaceAndDateTime(Place place, String selectedType, LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime) {
        String message = "";
        if (!(roomRepository.findRoomByPlaceAndRoomKind(place, selectedType).size() > reservationRepository.findResByRoomKindAndDateTime(selectedType, startDate, startTime, endDate, endTime).size())) {
            if (selectedType.contains("DESK")) {
                message = selectedType.replace("DESK", "데스크");
            } else if (selectedType.contains("MEETINGROOM")) {
                message = selectedType.replace("MEETINGROOM", "") + "인 회의실";
            } else if (selectedType.contains("OFFICE")) {
                message = selectedType.replace("OFFICE", "") + "평 사무실";
            }
            throw new NonexistentRestRoomForSelectedPlaceAndDateTime("NonexistentRestRoomForSelectedPlaceAndDateTime::: 선택하신 시간과 공간에 대해 빈 " + message + "이(가) 없습니다.");
        }
    }

    private void validRealDate(String selectedDate) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            dateFormat.setLenient(false);
            dateFormat.parse(selectedDate);
        } catch (ParseException e) {
            throw new InvalidDateException("InvalidDateError::: 선택하신 날짜는 존재하지 않습니다.");
        }
    }
    public void validSelectedDate(LocalDate startDate, LocalDate endDate) {
        if (startDate.isBefore(endDate)) {
            throw new InvalidDateException();
        }
    }

    public boolean isOpenToday(Place findPlace, LocalTime now) {
        return (findPlace.getPlaceStart().equals(now) || findPlace.getPlaceStart().isBefore(now)) && findPlace.getPlaceEnd().isAfter(now);
    }

    public boolean isOpenDaysByDate(String[] openDays, LocalDate date) {
        boolean state = true;
        for (String d : openDays) {
            if (TimeFormatter.toDayOfTheWeek(date).equals(d)) {
                state = false;
                break;
            }
        }
        return state;
    }
}
