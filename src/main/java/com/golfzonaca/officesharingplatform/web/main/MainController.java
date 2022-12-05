package com.golfzonaca.officesharingplatform.web.main;

import com.golfzonaca.officesharingplatform.service.place.PlaceService;
import com.golfzonaca.officesharingplatform.service.place.dto.PlaceListDto;
import com.golfzonaca.officesharingplatform.service.search.SearchService;
import com.golfzonaca.officesharingplatform.web.main.dto.request.RequestFilterData;
import com.golfzonaca.officesharingplatform.web.main.dto.request.RequestSearchData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MainController {

    private final PlaceService placeService;
    private final SearchService searchService;


    @GetMapping("/main")
    public Map<Integer, PlaceListDto> allPlace() {
        return placeService.processingMainPlaceData(placeService.findAllPlaces());
    }

    @PostMapping("/main/search")
    public Map<Integer, PlaceListDto> searchPlaces(@RequestBody @Validated RequestSearchData requestSearchData, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new IllegalArgumentException("검색어를 입력해주시기 바랍니다.");
        }
        if (requestSearchData.getSearchWord().equals("")) {
            return placeService.processingMainPlaceData(placeService.findAllPlaces());
        }
        return placeService.processingMainPlaceData(searchService.findPlaces(requestSearchData));
    }

    @PostMapping("/main/filter")
    public Map<Integer, PlaceListDto> filterPlaces(@RequestBody @Validated RequestFilterData requestFilterData, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new IllegalArgumentException("필터 조건을 하나 이상 선택해주시기 바랍니다.");
        }
        if (requestFilterData.getDay().equals("0") && requestFilterData.getStartTime().equals("24") && requestFilterData.getEndTime().equals("0") && requestFilterData.getCity().equals("0") && requestFilterData.getSubCity().equals("0") && requestFilterData.getType().equals("0")) {
            return placeService.processingMainPlaceData(placeService.findAllPlaces());
        }
        return placeService.processingMainPlaceData(searchService.filterPlaces(requestFilterData));
    }
}
