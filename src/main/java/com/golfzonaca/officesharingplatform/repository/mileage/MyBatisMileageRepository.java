package com.golfzonaca.officesharingplatform.repository.mileage;

import com.golfzonaca.officesharingplatform.domain.Mileage;
import com.golfzonaca.officesharingplatform.repository.mybatis.MileageMapper;
import com.golfzonaca.officesharingplatform.repository.mybatis.dto.MileageUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class MyBatisMileageRepository implements MileageRepository {
    private final MileageMapper mileageMapper;

    @Override
    public Mileage save(Mileage mileage) {
        mileageMapper.save(mileage);
        return mileage;
    }

    @Override
    public Mileage findById(long id) {
        return mileageMapper.findById(id);
    }

    @Override
    public void update(long id, MileageUpdateDto mileageUpdateDto) {
        mileageMapper.update(id, mileageUpdateDto);
    }

    @Override
    public List<Mileage> findAll() {
        return mileageMapper.findAll();
    }
}
