package com.golfzonaca.officesharingplatform.service.search;

import com.golfzonaca.officesharingplatform.web.search.form.SearchRequestData;
import com.golfzonaca.officesharingplatform.web.search.form.SearchResponseData;

import java.util.List;

public interface SearchService {
    List<SearchResponseData> findRoomByWord(SearchRequestData searchRequestData);
}
