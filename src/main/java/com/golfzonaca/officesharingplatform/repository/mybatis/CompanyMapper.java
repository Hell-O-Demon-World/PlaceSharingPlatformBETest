package com.golfzonaca.officesharingplatform.repository.mybatis;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CompanyMapper {
    String findOpenDaysById(long placeId);
}
