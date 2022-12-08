package com.golfzonaca.officesharingplatform.web.reservation;

import com.golfzonaca.officesharingplatform.annotation.TokenUserId;
import com.golfzonaca.officesharingplatform.domain.Place;
import com.golfzonaca.officesharingplatform.domain.User;
import com.golfzonaca.officesharingplatform.service.place.PlaceService;
import com.golfzonaca.officesharingplatform.service.reservation.ReservationService;
import com.golfzonaca.officesharingplatform.service.reservation.validation.ReservationRequestValidation;
import com.golfzonaca.officesharingplatform.service.user.UserService;
import com.golfzonaca.officesharingplatform.web.formatter.TimeFormatter;
import com.golfzonaca.officesharingplatform.web.reservation.dto.process.ProcessReservationData;
import com.golfzonaca.officesharingplatform.web.reservation.dto.request.ResRequestData;
import com.golfzonaca.officesharingplatform.web.reservation.dto.response.ReservationResponseData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;
    private final PlaceService placeService;
    private final UserService userService;
    private final ReservationRequestValidation reservationRequestValidation;

    @GetMapping("places/{placeId}/type/{typeName}/date/{inputDate}")
    public List<ReservationResponseData> selectedRoomType(@PathVariable Long placeId, @PathVariable String typeName, @PathVariable String inputDate) throws IOException {
        Place place = placeService.findById(placeId);
        reservationRequestValidation.validation(place, typeName, inputDate);

        return reservationService.getReservationResponseData(place, typeName, inputDate);
    }

    @GetMapping("places/{placeId}/type/{typeName}/date/{date}/startTime/{startTime}")
    public List<Integer> selectedDateTime(@PathVariable Long placeId, @PathVariable String typeName, @PathVariable String date, @PathVariable String startTime) {
        Place place = placeService.findById(placeId);
        reservationRequestValidation.validation(place, typeName, date, startTime);

        return reservationService.findAvailableTimes(place.getId(), typeName.toUpperCase(), TimeFormatter.toLocalDate(date), TimeFormatter.toLocalTime(startTime));
    }

    @PostMapping("places/{placeId}/book")
    public Map book(@TokenUserId Long userId, @PathVariable Long placeId, @Validated @RequestBody ResRequestData resRequestData) {
        ProcessReservationData processReservationData = getProcessReservationData(resRequestData);

        Place place = placeService.findById(placeId);
        User user = userService.findById(userId);
        reservationRequestValidation.validation(user, place, processReservationData);

        return reservationService.saveReservation(user, place, processReservationData);
    }

    private ProcessReservationData getProcessReservationData(ResRequestData resRequestData) {
        return new ProcessReservationData(resRequestData.getSelectedType(), TimeFormatter.toLocalDate(resRequestData.getStartDate()), TimeFormatter.toLocalDate(resRequestData.getEndDate()), TimeFormatter.toLocalTime(resRequestData.getStartTime()), TimeFormatter.toLocalTime(resRequestData.getEndTime()));
    }
}

