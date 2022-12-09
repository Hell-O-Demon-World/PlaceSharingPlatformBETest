package com.golfzonaca.officesharingplatform.service.search;

import com.golfzonaca.officesharingplatform.domain.Place;
import com.golfzonaca.officesharingplatform.repository.place.PlaceRepository;
import com.golfzonaca.officesharingplatform.web.main.dto.request.RequestSearchData;
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
    public List<Place> filterPlaces(String day, String startTime, String endTime, String city, String subCity, String type) {
        return placeRepository.filterPlaces(day, startTime, endTime, city, subCity, type);
    }
}