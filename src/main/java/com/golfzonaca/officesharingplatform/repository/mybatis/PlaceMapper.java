package com.golfzonaca.officesharingplatform.repository.mybatis;

import com.golfzonaca.officesharingplatform.domain.Place;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PlaceMapper {

    Place findById(long id);
}
