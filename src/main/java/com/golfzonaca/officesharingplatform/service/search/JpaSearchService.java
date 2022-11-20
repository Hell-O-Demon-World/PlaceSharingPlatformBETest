package com.golfzonaca.officesharingplatform.service.search;

import com.golfzonaca.officesharingplatform.domain.Place;
import com.golfzonaca.officesharingplatform.repository.place.PlaceRepository;
import com.golfzonaca.officesharingplatform.web.search.form.SearchRequestData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JpaSearchService implements SearchService {

    private final PlaceRepository placeRepository;

    @Override
    public List<Place> findPlaces(Optional<SearchRequestData> searchRequestData) {
        return placeRepository.findPlaces(searchRequestData);
    }
}
