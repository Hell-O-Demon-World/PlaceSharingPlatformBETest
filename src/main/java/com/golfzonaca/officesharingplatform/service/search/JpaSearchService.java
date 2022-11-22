package com.golfzonaca.officesharingplatform.service.search;

import com.golfzonaca.officesharingplatform.domain.Place;
import com.golfzonaca.officesharingplatform.repository.place.PlaceRepository;
import com.golfzonaca.officesharingplatform.web.search.dto.RequestFilterData;
import com.golfzonaca.officesharingplatform.web.search.dto.RequestSearchData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JpaSearchService implements SearchService {

    private final PlaceRepository placeRepository;

    @Override
    public List<Place> findPlaces(RequestSearchData requestSearchData) {
        return placeRepository.findPlaces(requestSearchData);
    }

    @Override
    public List<Place> filterPlaces(RequestFilterData requestFilterData) {
        return placeRepository.filterPlaces(requestFilterData);
    }
}
