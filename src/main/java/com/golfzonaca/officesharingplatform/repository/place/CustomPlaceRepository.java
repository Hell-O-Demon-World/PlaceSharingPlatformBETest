package com.golfzonaca.officesharingplatform.repository.place;

import com.golfzonaca.officesharingplatform.domain.Place;
import com.golfzonaca.officesharingplatform.exception.NonExistedPlaceException;
import com.golfzonaca.officesharingplatform.web.main.dto.request.RequestFilterData;
import com.golfzonaca.officesharingplatform.web.main.dto.request.RequestSearchData;
import com.querydsl.core.Tuple;
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
    public List<Place> findAllPlaces() {
        return jpaRepository.findAll();
    }

    @Override
    public Place findById(long id) {
        return jpaRepository.findById(id).orElseThrow(NonExistedPlaceException::new);
    }

    @Override
    public List<Place> findPlaces(RequestSearchData requestSearchData) {
        return queryPlaceRepository.findPlaces(requestSearchData);
    }

    @Override
    public List<Place> filterPlaces(RequestFilterData requestFilterData) {
        return queryPlaceRepository.filterPlaces(requestFilterData);
    }

    @Override
    public String findOpenDayById(Long id) {
        return queryPlaceRepository.findOpenDayById(id).orElseThrow(NonExistedPlaceException::new);
    }

    @Override
    public Tuple findStartAndEndTimeById(Long id) {
        return queryPlaceRepository.findStartAndEndTimeById(id);
    }
}
