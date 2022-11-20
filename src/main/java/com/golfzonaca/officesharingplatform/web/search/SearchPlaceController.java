package com.golfzonaca.officesharingplatform.web.search;

import com.golfzonaca.officesharingplatform.domain.Place;
import com.golfzonaca.officesharingplatform.service.search.SearchService;
import com.golfzonaca.officesharingplatform.web.search.dto.SearchRequestData;
import com.golfzonaca.officesharingplatform.web.search.dto.SearchResponseData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SearchPlaceController {

    private final SearchService searchService;

    @PostMapping("/main/search")
    public List<SearchResponseData> findPlaces(@RequestBody SearchRequestData searchRequestData) {
        List<Place> result = searchService.findPlaces(searchRequestData);
        List<SearchResponseData> places = new ArrayList<>();
        for (Place place : result) {
            places.add(new SearchResponseData(place.getId(), place.getPlaceName(), place.getAddress(), place.getOpenDays(), place.getPlaceStart(), place.getPlaceEnd(), place.getPlaceAddInfo()));
        }
        return places;
    }
}
