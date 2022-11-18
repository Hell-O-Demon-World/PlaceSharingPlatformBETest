package com.golfzonaca.officesharingplatform.repository.mileage;

import com.golfzonaca.officesharingplatform.domain.Mileage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
@RequiredArgsConstructor
public class JpaMileageRepository implements MileageRepository{
    private final SpringDataJpaMileageRepository repository;


    @Override
    public Mileage save(Mileage mileage) {
        return repository.save(mileage);
    }

    @Override
    public Mileage findByID(Long id) {
        return repository.findById(id).get();
    }
}
