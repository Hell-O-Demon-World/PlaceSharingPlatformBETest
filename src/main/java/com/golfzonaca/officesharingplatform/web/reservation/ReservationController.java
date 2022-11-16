package com.golfzonaca.officesharingplatform.web.reservation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.golfzonaca.officesharingplatform.service.reservation.ReservationService;
import com.golfzonaca.officesharingplatform.web.reservation.form.ResRequestData;
import com.golfzonaca.officesharingplatform.web.reservation.form.SelectedDateTimeForm;
import com.golfzonaca.officesharingplatform.web.reservation.form.TimeListForm;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping("places/{placeId}")
    public JsonObject findRoom(@PathVariable long placeId) {
        JsonObject responseData = reservationService.findRoom(placeId);
        return responseData;
    }

    @PostMapping("/places/{placeId}")
    public TimeListForm selectedDateTime(@PathVariable String placeId, @Valid @RequestBody SelectedDateTimeForm selectedDateTimeForm) {
        TimeListForm timeListForm = new TimeListForm();

        timeListForm.setTimeList(reservationService.getReservationTimeList(Long.parseLong(placeId), selectedDateTimeForm));
        return timeListForm;
    }

    @PostMapping("places/{placeId}/book")
    public Map book(@PathVariable long placeId, @RequestBody ResRequestData resRequestData) throws JsonProcessingException {
        Map<String, String> errorMap = reservationService.ResRequestValidation(placeId, resRequestData);
        return errorMap;
    }
}

