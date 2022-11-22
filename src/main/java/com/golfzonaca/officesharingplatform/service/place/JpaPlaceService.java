package com.golfzonaca.officesharingplatform.service.place;

import com.golfzonaca.officesharingplatform.domain.Place;
import com.golfzonaca.officesharingplatform.repository.place.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JpaPlaceService implements PlaceService {

    private final PlaceRepository placeRepository;

    @Override
    public Place findById(long placeId) {
        Optional<Place> findPlace = placeRepository.findById(placeId);
        return findPlace.orElse(null);
    }
}
