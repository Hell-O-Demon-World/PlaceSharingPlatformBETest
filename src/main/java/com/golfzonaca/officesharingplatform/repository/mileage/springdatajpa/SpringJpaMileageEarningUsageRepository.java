package com.golfzonaca.officesharingplatform.repository.mileage.springdatajpa;

import com.golfzonaca.officesharingplatform.domain.MileageEarningUsage;
import com.golfzonaca.officesharingplatform.domain.MileageTransactionUsage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringJpaMileageEarningUsageRepository extends JpaRepository<MileageEarningUsage, Long> {
}
