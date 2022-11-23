package com.golfzonaca.officesharingplatform.web.search;

import com.golfzonaca.officesharingplatform.domain.Place;
import com.golfzonaca.officesharingplatform.service.place.PlaceService;
import com.golfzonaca.officesharingplatform.service.search.SearchService;
import com.golfzonaca.officesharingplatform.web.search.dto.request.RequestFilterData;
import com.golfzonaca.officesharingplatform.web.search.dto.request.RequestSearchData;
import com.golfzonaca.officesharingplatform.web.search.dto.response.ResponseData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
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
    private final PlaceService placeService;

    @PostMapping("/main/search")
    public List<ResponseData> searchPlaces(@RequestBody @Validated RequestSearchData requestSearchData, BindingResult bindingResult) {
        if (requestSearchData.getSearchWord().equals("")) {
            return response(placeService.findAllPlaces());
        }
        return response(searchService.findPlaces(requestSearchData));
    }

    @PostMapping("/main/filter")
    public List<ResponseData> filterPlaces(@RequestBody @Validated RequestFilterData requestFilterData, BindingResult bindingResult) {
        return response(searchService.filterPlaces(requestFilterData));
    }

    private List<ResponseData> response(List<Place> resultList) {
        List<ResponseData> places = new ArrayList<>();
        for (Place place : resultList) {
            places.add(new ResponseData(place.getId(), place.getPlaceName(), place.getAddress().getAddress(), place.getPlaceAddInfo()));
        }
        return places;
    }

}
