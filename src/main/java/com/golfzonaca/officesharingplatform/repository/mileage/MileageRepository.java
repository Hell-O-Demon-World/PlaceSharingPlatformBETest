package com.golfzonaca.officesharingplatform.repository.mileage;

import com.golfzonaca.officesharingplatform.domain.Mileage;

public interface MileageRepository {
    Mileage save(Mileage mileage);

    Mileage findByID(Long id);
}
