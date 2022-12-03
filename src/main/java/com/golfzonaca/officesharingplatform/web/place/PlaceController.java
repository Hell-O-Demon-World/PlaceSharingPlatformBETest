package com.golfzonaca.officesharingplatform.web.place;

import com.golfzonaca.officesharingplatform.service.place.PlaceService;
import com.golfzonaca.officesharingplatform.service.place.dto.PlaceDetailsInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PlaceController {
    private final PlaceService placeService;

    @GetMapping("places/{placeId}")
    public PlaceDetailsInfo placeDetailsInfo(@PathVariable long placeId) {
        return placeService.getPlaceDetailsInfo(placeId);
    }
}