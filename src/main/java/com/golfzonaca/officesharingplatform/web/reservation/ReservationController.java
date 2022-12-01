package com.golfzonaca.officesharingplatform.web.reservation;

import com.golfzonaca.officesharingplatform.annotation.TokenUserId;
import com.golfzonaca.officesharingplatform.domain.Place;
import com.golfzonaca.officesharingplatform.domain.User;
import com.golfzonaca.officesharingplatform.repository.roomkind.RoomKindRepository;
import com.golfzonaca.officesharingplatform.service.place.PlaceService;
import com.golfzonaca.officesharingplatform.service.reservation.ReservationService;
import com.golfzonaca.officesharingplatform.service.user.UserService;
import com.golfzonaca.officesharingplatform.web.formatter.TimeFormatter;
import com.golfzonaca.officesharingplatform.web.reservation.dto.process.ProcessReservationData;
import com.golfzonaca.officesharingplatform.web.reservation.dto.request.ResRequestData;
import com.golfzonaca.officesharingplatform.web.reservation.dto.response.ReservationResponseData;
import com.golfzonaca.officesharingplatform.web.reservation.dto.response.ReservationResponseTypeForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final PlaceService placeService;
    private final UserService userService;
    private final RoomKindRepository roomKindRepository;

    @GetMapping("places/{placeId}")
    public ReservationResponseTypeForm findRoom(@PathVariable long placeId) {
        return reservationService.findRoom(placeId);
    }

    @GetMapping("places/{placeId}/type/{typeName}/date/{inputDate}")
    public Map selectedRoomType(@PathVariable Long placeId, @PathVariable String typeName, @PathVariable String inputDate) {
        Map<String, Object> response = new LinkedHashMap<>();
        Map<String, String> errorMap = new HashMap<>();
        String selectedType = typeName.toUpperCase();
        if (!placeService.isExistPlace(placeId)) {
            errorMap.put("placeError", "선택하신 공간은 존재하지 않습니다.");
            response.put("errorMap", errorMap);
            return response;
        } else if (!roomKindRepository.findByRoomType(selectedType)) {
            errorMap.put("roomTypeError", "선택하신 공간유형은 존재하지 않습니다.");
            response.put("errorMap", errorMap);
            return response;
        } else if (!dateValidate(inputDate)) {
            errorMap.put("dateError", "선택하신 날짜는 존재하지 않습니다.");
            response.put("errorMap", errorMap);
            return response;
        }
        List<ReservationResponseData> result = reservationService.getReservationResponseData(placeId, selectedType, inputDate);
        response.put("data", result);
        response.put("errorMap", errorMap);
        return response;
    }

    @GetMapping("places/{placeId}/type/{typeName}/date/{date}/startTime/{startTime}")
    public Map selectedDateTime(@PathVariable Long placeId, @PathVariable String typeName, @PathVariable String date, @PathVariable String startTime) {
        Map<String, Object> response = new LinkedHashMap<>();
        Map<String, String> errorMap = new HashMap<>();
        if (!placeService.isExistPlace(placeId)) {
            errorMap.put("placeError", "선택하신 공간은 존재하지 않습니다.");
            response.put("errorMap", errorMap);
            return response;
        } else if (!dateValidate(date)) {
            errorMap.put("dateError", "선택하신 날짜는 존재하지 않습니다.");
            response.put("errorMap", errorMap);
            return response;
        } else if (!placeService.isOpenDay(placeId, date)) {
            errorMap.put("placeError", "선택하신 날짜는 영업 일이 아닙니다.");
            response.put("errorMap", errorMap);
            return response;
        } else if (!placeService.isOpenToday(placeId, startTime)) {
            errorMap.put("placeError", "선택하신 시간은 영업 시간이 아닙니다.");
            response.put("errorMap", errorMap);
            return response;
        } else if (!roomKindRepository.findByRoomType(typeName.toUpperCase())) {
            errorMap.put("roomTypeError", "선택하신 공간유형은 존재하지 않습니다.");
            response.put("errorMap", errorMap);
            return response;
        } else if (typeName.toUpperCase().contains("OFFICE")) {
            errorMap.put("roomTypeError", "지원하지 않는 공간유형 입니다.");
            response.put("errorMap", errorMap);
            return response;
        }
        List<Integer> result = reservationService.findAvailableTimes(placeId, typeName.toUpperCase(), TimeFormatter.toLocalDate(date), TimeFormatter.toLocalTime(startTime));
        response.put("timeList", result);
        response.put("errorMap", errorMap);
        return response;
    }

    @PostMapping("places/{placeId}/book")
    public Map book(@TokenUserId Long userId, @PathVariable Long placeId, @Validated @RequestBody ResRequestData resRequestData) {
        Map<String, Object> response = new LinkedHashMap<>();
        Map<String, String> errorMap = new HashMap<>();
        ProcessReservationData processReservationData = getProcessReservationData(resRequestData);
        if (!placeService.isExistPlace(placeId)) {
            errorMap.put("placeError", "선택하신 공간은 존재하지 않습니다.");
            response.put("errorMap", errorMap);
            return response;
        } else if (!placeService.selectedDateValidation(resRequestData.getStartDate(), resRequestData.getEndDate())) {
            errorMap.put("placeError", "선택하신 날짜는 유효하지 않습니다.");
            response.put("errorMap", errorMap);
            return response;
        } else if (!placeService.isOpenDay(placeId, resRequestData.getStartDate()) && !placeService.isOpenDay(placeId, resRequestData.getEndDate())) {
            errorMap.put("placeError", "선택하신 날짜는 영업 일이 아닙니다.");
            response.put("errorMap", errorMap);
            return response;
        } else if (!placeService.isOpenToday(placeId, resRequestData.getStartTime())) {
            errorMap.put("placeError", "선택하신 시간은 영업 시간이 아닙니다.");
            response.put("errorMap", errorMap);
            return response;
        } else if (!roomKindRepository.findByRoomType(resRequestData.getSelectedType())) {
            errorMap.put("roomTypeError", "선택하신 공간유형은 존재하지 않습니다.");
            response.put("errorMap", errorMap);
            return response;
        }

        User user = userService.findById(userId);
        if (user == null) {
            response.put("InvalidUserError", "등록되지 않은 회원입니다.");
            return response;
        }

        Place place = placeService.findById(placeId);
        if (place == null) {
            response.put("NonexistentPlaceError", "존재하지 않는 공간입니다.");
            return response;
        }

        errorMap = reservationService.validation(errorMap, user, place, processReservationData);

        if (errorMap.isEmpty()) {
            return reservationService.saveReservation(user, place, processReservationData);
        }
        response.put("errorMap", errorMap);
        return response;
    }

    private ProcessReservationData getProcessReservationData(ResRequestData resRequestData) {
        return new ProcessReservationData(resRequestData.getSelectedType(), TimeFormatter.toLocalDate(resRequestData.getStartDate()), TimeFormatter.toLocalDate(resRequestData.getEndDate()), TimeFormatter.toLocalTime(resRequestData.getStartTime()), TimeFormatter.toLocalTime(resRequestData.getEndTime()));
    }

    private boolean dateValidate(String selectedDate) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            dateFormat.setLenient(false);
            dateFormat.parse(selectedDate);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
}

