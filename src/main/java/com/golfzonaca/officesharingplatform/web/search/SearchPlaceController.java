package com.golfzonaca.officesharingplatform.web.search;

import com.golfzonaca.officesharingplatform.service.search.SearchService;
import com.golfzonaca.officesharingplatform.web.search.form.SearchRequestData;
import com.golfzonaca.officesharingplatform.web.search.form.SearchResponseData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SearchPlaceController {

    private final SearchService searchService;

    @PostMapping("/main/search")
    public List<SearchResponseData> findRoomByWord(@RequestBody SearchRequestData searchRequestData) {
        List<SearchResponseData> responseDataList = searchService.findRoomByWord(searchRequestData);
        return responseDataList;
    }
}
