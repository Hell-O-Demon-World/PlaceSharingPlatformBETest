package com.golfzonaca.officesharingplatform.repository.mileage;

import com.golfzonaca.officesharingplatform.domain.Mileage;
import com.golfzonaca.officesharingplatform.repository.mybatis.MileageMapper;
import com.golfzonaca.officesharingplatform.repository.mybatis.dto.MileageUpdateDto;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class MyBatisMileageRepository {
    private final MileageMapper mileageMapper;

    public Mileage save(Mileage mileage) {
        mileageMapper.save(mileage);
        return mileage;
    }

    public Mileage findById(long id) {
        return mileageMapper.findById(id);
    }

    public void update(long id, MileageUpdateDto mileageUpdateDto) {
        mileageMapper.update(id, mileageUpdateDto);
    }

    public List<Mileage> findAll() {
        return mileageMapper.findAll();
    }
}
