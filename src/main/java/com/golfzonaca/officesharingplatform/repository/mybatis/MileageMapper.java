package com.golfzonaca.officesharingplatform.repository.mybatis;

import com.golfzonaca.officesharingplatform.domain.Mileage;
import com.golfzonaca.officesharingplatform.repository.mybatis.dto.MileageUpdateDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MileageMapper {
    void save(Mileage mileage);

    Mileage findById(long id);

    void update(@Param("id") long id, @Param("updateParam") MileageUpdateDto updateParam);

    List<Mileage> findAll();

}
