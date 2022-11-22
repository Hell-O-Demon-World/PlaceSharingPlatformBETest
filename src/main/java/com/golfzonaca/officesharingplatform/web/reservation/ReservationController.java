package com.golfzonaca.officesharingplatform.web.reservation;

import com.golfzonaca.officesharingplatform.annotation.TokenUserId;
import com.golfzonaca.officesharingplatform.domain.Place;
import com.golfzonaca.officesharingplatform.domain.User;
import com.golfzonaca.officesharingplatform.service.place.PlaceService;
import com.golfzonaca.officesharingplatform.service.reservation.ReservationService;
import com.golfzonaca.officesharingplatform.service.user.UserService;
import com.golfzonaca.officesharingplatform.web.reservation.form.ResRequestData;
import com.golfzonaca.officesharingplatform.web.reservation.form.SelectedTypeAndDayForm;
import com.golfzonaca.officesharingplatform.web.reservation.form.TimeListForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final PlaceService placeService;
    private final UserService userService;

    @GetMapping("places/{placeId}")
    public Map<String, String> findRoom(@PathVariable long placeId) {
        return reservationService.findRoom(placeId);
    }

    @PostMapping("/places/{placeId}")
    public TimeListForm selectedDateTime(@PathVariable long placeId, @Valid @RequestBody SelectedTypeAndDayForm selectedTypeAndDayForm) {
        return new TimeListForm(reservationService.getReservationTimeList(placeId, selectedTypeAndDayForm));
    }

    @PostMapping("places/{placeId}/book")
    public Map book(@TokenUserId Long userId, @PathVariable long placeId, @RequestBody ResRequestData resRequestData) {
        Map<String, String> response = new LinkedHashMap<>();

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

        response = reservationService.validation(response, user, place, resRequestData);

        if (response.isEmpty()) {
            response = reservationService.saveReservation(response, user, place, resRequestData);
            return response;
        }
        return response;
    }
}

