package com.golfzonaca.officesharingplatform.repository.mileage;

import com.golfzonaca.officesharingplatform.domain.*;

import java.time.LocalDateTime;
import java.util.List;

public interface MileageRepository {
    Mileage save(Mileage mileage);
    MileageUpdate save(MileageUpdate mileageUpdate);
    MileagePaymentUpdate save(MileagePaymentUpdate mileagePaymentUpdate);
    MileageTransactionUsage save(MileageTransactionUsage mileageTransactionUsage);
    MileageExpiredHistory save(MileageExpiredHistory mileageExpiredHistory);
    Mileage findByID(Long id);
    MileagePaymentUpdate findMileageByPayment(Payment payment);
    List<MileageTransactionUsage> findTransactionUsageMileageByPaymentMileage(MileagePaymentUpdate mileagePaymentUpdate);
    MileageExpiredHistory findExpiredMileage(MileageTransactionUsage mileageTransactionUsage);
    Mileage findByUser(User user);
    List<MileageUpdate> findMileageUpdateAll();
    List<MileageUpdate> findMileageUpdateAllLikeUserAndExpireDate(Mileage mileage, LocalDateTime localDateTime);

    List<MileageUpdate> findAllMileageUpdateByMileage(Mileage findMileage);

    MileagePaymentUpdate findMileagePaymentByMileageUpdate(MileageUpdate mileageUpdate);
}
