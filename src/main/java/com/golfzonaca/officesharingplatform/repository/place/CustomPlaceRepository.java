package com.golfzonaca.officesharingplatform.repository.place;

import com.golfzonaca.officesharingplatform.domain.Place;
import com.golfzonaca.officesharingplatform.domain.type.RoomType;
import com.golfzonaca.officesharingplatform.exception.NonExistedPlaceException;
import com.golfzonaca.officesharingplatform.web.main.dto.request.RequestSearchData;
import com.querydsl.core.Tuple;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

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
        return jpaRepository.findById(id).orElseThrow(() -> new NonExistedPlaceException("NonExistedPlaceException::: 공간이 존재하지 않습니다."));
    }

    @Override
    public List<Place> findPlaces(RequestSearchData requestSearchData) {
        return queryPlaceRepository.findPlaces(requestSearchData);
    }

    @Override
    public List<Place> filterPlaces(String day, LocalTime startTime, LocalTime endTime, String city, String subCity, List<RoomType> roomTypeList) {
        return queryPlaceRepository.filterPlaces(day, startTime, endTime, city, subCity, roomTypeList);
    }

    @Override
    public String findOpenDayById(Long id) {
        return queryPlaceRepository.findOpenDayById(id).orElseThrow(() -> new NonExistedPlaceException("NonExistedPlaceException::: 공간이 존재하지 않습니다."));
    }

    @Override
    public Tuple findStartAndEndTimeById(Long id) {
        return queryPlaceRepository.findStartAndEndTimeById(id);
    }
}
