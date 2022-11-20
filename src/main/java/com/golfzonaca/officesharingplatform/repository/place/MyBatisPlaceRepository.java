package com.golfzonaca.officesharingplatform.repository.place;

import com.golfzonaca.officesharingplatform.domain.Place;
import com.golfzonaca.officesharingplatform.repository.mybatis.PlaceMapper;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MyBatisPlaceRepository {
    private final PlaceMapper placeMapper;

    public Place findById(long id) {
        return placeMapper.findById(id);
    }
}
