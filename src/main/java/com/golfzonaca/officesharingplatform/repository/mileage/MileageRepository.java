package com.golfzonaca.officesharingplatform.repository.mileage;

import com.golfzonaca.officesharingplatform.domain.Mileage;
import com.golfzonaca.officesharingplatform.repository.mybatis.dto.MileageUpdateDto;

import java.util.List;

public interface MileageRepository {
    Mileage save(Mileage mileage);

    Mileage findById(long id);

    void update(long id, MileageUpdateDto mileageUpdateDto);

    List<Mileage> findAll();

}
