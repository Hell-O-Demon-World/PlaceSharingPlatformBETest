package com.golfzonaca.officesharingplatform.web.main;

import com.golfzonaca.officesharingplatform.service.place.PlaceService;
import com.golfzonaca.officesharingplatform.service.place.dto.response.PlaceDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MainController {

    private final PlaceService placeService;

    @GetMapping("/main")
    public Map<Integer, PlaceDto> allPlace() {
        return placeService.processingMainPlaceData();
    }
}
