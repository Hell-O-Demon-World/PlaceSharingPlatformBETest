package com.golfzonaca.officesharingplatform.repository.mileage.springdatajpa;

import com.golfzonaca.officesharingplatform.domain.MileageExpiredHistory;
import com.golfzonaca.officesharingplatform.domain.MileageTransactionUsage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringJpaMileageExpiredRepository extends JpaRepository<MileageExpiredHistory, Long> {
    MileageExpiredHistory findFirstByMileageTransactionUsage(MileageTransactionUsage mileageTransactionUsage);
}
