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
import com.golfzonaca.officesharingplatform.web.reservation.form.SelectedTypeAndDayForm;
import com.golfzonaca.officesharingplatform.web.reservation.form.TimeListForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final PlaceService placeService;
    private final UserService userService;
    private final RoomKindRepository roomKindRepository;

    @GetMapping("places/{placeId}")
    public Map<String, String> findRoom(@PathVariable long placeId) {
        return reservationService.findRoom(placeId);
    }

    @PostMapping("/places/{placeId}")
    public TimeListForm selectedDateTime(@PathVariable long placeId, @Valid @RequestBody SelectedTypeAndDayForm selectedTypeAndDayForm) {
        List<Integer> resultTimeList = new ArrayList<>();
        Map<String, String> errorMap = new HashMap<>();
        if (!placeService.isExistPlace(placeId)) {
            errorMap.put("placeError", "선택하신 공간은 존재하지 않습니다.");
            return new TimeListForm(resultTimeList, errorMap);
        } else if (!placeService.isOpenDay(placeId, selectedTypeAndDayForm.getDay())) {
            errorMap.put("placeError", "선택하신 날짜는 영업 일이 아닙니다.");
            return new TimeListForm(resultTimeList, errorMap);
        } else if (!placeService.isOpenToday(placeId, selectedTypeAndDayForm.getStartTime())) {
            errorMap.put("placeError", "선택하신 시간은 영업 시간이 아닙니다.");
            return new TimeListForm(resultTimeList, errorMap);
        } else if (!roomKindRepository.findByRoomType(selectedTypeAndDayForm.getSelectedType())) {
            errorMap.put("roomTypeError", "선택하신 공간유형은 존재하지 않습니다.");
            return new TimeListForm(resultTimeList, errorMap);
        }
        resultTimeList = reservationService.getReservationTimeList(placeId, selectedTypeAndDayForm);

        return new TimeListForm(resultTimeList, errorMap);
    }

    @PostMapping("places/{placeId}/book")
    public Map book(@TokenUserId Long userId, @PathVariable long placeId, @Validated @RequestBody ResRequestData resRequestData) {
        Map<String, String> response = new LinkedHashMap<>();
        ProcessReservationData processReservationData = getProcessReservationData(resRequestData);

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

        response = reservationService.validation(response, user, place, processReservationData);

        if (response.isEmpty()) {
            response = reservationService.saveReservation(response, user, place, processReservationData);
            return response;
        }
        return response;
    }

    private ProcessReservationData getProcessReservationData(ResRequestData resRequestData) {
        return new ProcessReservationData(resRequestData.getSelectedType(), TimeFormatter.toLocalDate(resRequestData.getDate()), TimeFormatter.toLocalTime(resRequestData.getStartTime()), TimeFormatter.toLocalTime(resRequestData.getEndTime()));
    }
}

