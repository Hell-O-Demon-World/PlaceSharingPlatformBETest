package com.golfzonaca.officesharingplatform.service.search;

import com.golfzonaca.officesharingplatform.domain.Place;
import com.golfzonaca.officesharingplatform.repository.address.AddressRepository;
import com.golfzonaca.officesharingplatform.repository.place.PlaceRepository;
import com.golfzonaca.officesharingplatform.web.search.form.SearchRequestData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MyBatisSearchService implements SearchService {

    private final AddressRepository addressRepository;
    private final PlaceRepository placeRepository;

    public List<Place> findByPlaceNameLike(SearchRequestData searchRequestData) {
        List<Place> placeSearchList = placeRepository.findByPlaceNameLike(searchRequestData.getSearchWord());

        return placeSearchList;
    }
}
