package com.golfzonaca.officesharingplatform.web.search;

import com.golfzonaca.officesharingplatform.domain.Place;
import com.golfzonaca.officesharingplatform.service.place.PlaceService;
import com.golfzonaca.officesharingplatform.service.search.SearchService;
import com.golfzonaca.officesharingplatform.web.search.dto.request.RequestFilterData;
import com.golfzonaca.officesharingplatform.web.search.dto.request.RequestSearchData;
import com.golfzonaca.officesharingplatform.web.search.dto.response.ResponseData;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SearchPlaceController {

    private final SearchService searchService;
    private final PlaceService placeService;

    @PostMapping("/main/search")
    public JsonObject searchPlaces(@RequestBody @Validated RequestSearchData requestSearchData, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return abnormalResponse("requestSearchDataError", "검색어를 입력해주시기 바랍니다.");
        }
        if (requestSearchData.getSearchWord().equals("")) {
            return normalResponse(placeService.findAllPlaces());
        }
        return normalResponse(searchService.findPlaces(requestSearchData));
    }

    @PostMapping("/main/filter")
    public JsonObject filterPlaces(@RequestBody @Validated RequestFilterData requestFilterData, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return abnormalResponse("requestFilterDataError", "필터 조건을 하나 이상 선택해주시기 바랍니다.");
        }
        return normalResponse(searchService.filterPlaces(requestFilterData));
    }

    @GetMapping("/main/test")
    public String testPlaces() {
        List<Place> places = placeService.findAllPlaces();
        for (Place place : places) {
            log.info("place.getRatePoint().getId()={}, place.getRatePoint().getRatingPoint()={}", place.getRatePoint().getId(), place.getRatePoint().getRatingPoint());
        }
        return "ok";
    }

    private JsonObject normalResponse(List<Place> resultList) {
        JsonObject response = new JsonObject();

        for (Place place : resultList) {
            response.addProperty(place.getId().toString(), new ResponseData(place.getId(), place.getPlaceName(), place.getAddress().getAddress(), place.getPlaceAddInfo()).toString());
        }
        return response;
    }

    private JsonObject abnormalResponse(String key, String value) {
        JsonObject response = new JsonObject();
        response.addProperty(key, value);
        return response;
    }
}
