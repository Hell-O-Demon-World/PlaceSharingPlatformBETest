package com.golfzonaca.officesharingplatform.repository.place;

import com.golfzonaca.officesharingplatform.domain.Place;
import com.golfzonaca.officesharingplatform.web.search.dto.SearchRequestData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
@RequiredArgsConstructor
public class CustomPlaceRepository implements PlaceRepository {

    private final SpringJpaPlaceRepository jpaRepository;
    private final QueryPlaceRepository queryPlaceRepository;

    @Override
    public Optional<Place> findById(long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<Place> findPlaces(SearchRequestData searchRequestData) {
        return queryPlaceRepository.findPlaces(searchRequestData);
    }


}
