package com.golfzonaca.officesharingplatform.repository.place;

import com.golfzonaca.officesharingplatform.domain.Place;
import com.golfzonaca.officesharingplatform.repository.mybatis.PlaceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MyBatisPlaceRepository implements PlaceRepository{
    private final PlaceMapper placeMapper;
    @Override
    public Place findById(long id) {
        return placeMapper.findById(id);
    }
}
