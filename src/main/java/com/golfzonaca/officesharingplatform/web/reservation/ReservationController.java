package com.golfzonaca.officesharingplatform.web.reservation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.golfzonaca.officesharingplatform.annotation.TokenUserId;
import com.golfzonaca.officesharingplatform.config.auth.token.JwtManager;
import com.golfzonaca.officesharingplatform.domain.Place;
import com.golfzonaca.officesharingplatform.domain.User;
import com.golfzonaca.officesharingplatform.repository.place.PlaceRepository;
import com.golfzonaca.officesharingplatform.repository.user.UserRepository;
import com.golfzonaca.officesharingplatform.service.reservation.ReservationService;
import com.golfzonaca.officesharingplatform.web.reservation.form.ResRequestData;
import com.golfzonaca.officesharingplatform.web.reservation.form.SelectedDateTimeForm;
import com.golfzonaca.officesharingplatform.web.reservation.form.TimeListForm;
import com.golfzonaca.officesharingplatform.web.reservation.validation.ReservationValidation;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final ReservationValidation reservationValidation;
    private final PlaceRepository placeRepository;
    private final UserRepository userRepository;

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
    public Map book(@TokenUserId Long userId, @PathVariable long placeId, @RequestBody ResRequestData resRequestData) {
        Map<String, String> errorMap = new LinkedHashMap<>();

        Optional<Place> findPlace = placeRepository.findById(placeId);
        if (findPlace.isEmpty()) {
            errorMap.put("NonexistentPlaceError", "존재하지 않는 공간입니다.");
            return errorMap;
        }

        Optional<User> findUser = userRepository.findById(userId);
        if (findUser.isEmpty()) {
            errorMap.put("InvalidUserError", "등록되지 않은 회원입니다.");
            return errorMap;
        }

        errorMap = reservationValidation.validation(errorMap, findUser.get(), findPlace.get(), resRequestData);
        
        if (errorMap.isEmpty()) {
            errorMap = reservationService.reservation(errorMap, findUser.get(), findPlace.get(), resRequestData);
            return errorMap;
        }
        return errorMap;
    }
}

