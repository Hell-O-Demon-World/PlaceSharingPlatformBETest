package com.golfzonaca.officesharingplatform.service.mileage;

import com.golfzonaca.officesharingplatform.domain.*;
import com.golfzonaca.officesharingplatform.domain.type.MileagePaymentReason;
import com.golfzonaca.officesharingplatform.domain.type.MileageStatusType;
import com.golfzonaca.officesharingplatform.repository.mileage.MileageRepository;
import com.golfzonaca.officesharingplatform.service.payment.MileageTimeSetter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SpringJpaDslMileageService implements MileageService {
    private final MileageRepository mileageRepository;
    private final static long INITIAL_POINT = 0;

    @Override
    public Mileage join() {
        Mileage mileage = Mileage.builder()
                .point(INITIAL_POINT)
                .latestUpdateDate(MileageTimeSetter.currentDateTime())
                .build();
        Mileage saveMileage = mileageRepository.save(mileage);
        MileageUpdate mileageUpdate = MileageUpdate.builder()
                .mileage(saveMileage)
                .statusType(MileageStatusType.NEW_MEMBER)
                .updatePoint(INITIAL_POINT)
                .updateDate(MileageTimeSetter.currentDateTime())
                .expireDate(MileageTimeSetter.expiredDateTime())
                .build();
        mileageRepository.save(mileageUpdate);
        return mileage;
    }

    @Override
    public void savingFullPaymentMileage(Payment payment) {
        Mileage mileage = payment.getReservation().getUser().getMileage();
        long currentPayMileage = payment.getSavedMileage();
        MileageUpdate savedMileageUpdate = saveMileageUpdate(mileage, currentPayMileage, MileageStatusType.EARNING);
        saveMileagePaymentUpdate(payment, currentPayMileage, savedMileageUpdate, MileagePaymentReason.FULL_PAYMENT);
        saveMileageEarningUsage(currentPayMileage, savedMileageUpdate);

        mileage.addPoint(currentPayMileage);
    }

    private MileageUpdate saveMileageUpdate(Mileage mileage, long currentPayMileage, MileageStatusType mileageStatusType) {
        MileageUpdate mileageUpdate = MileageUpdate.builder()
                .mileage(mileage)
                .statusType(mileageStatusType)
                .updatePoint(currentPayMileage)
                .updateDate(MileageTimeSetter.currentDateTime())
                .expireDate(MileageTimeSetter.expiredDateTime())
                .build();
        return mileageRepository.save(mileageUpdate);
    }

    private void saveMileageEarningUsage(long currentPayMileage, MileageUpdate mileageUpdate) {
        MileageEarningUsage mileageEarningUsage = MileageEarningUsage.builder()
                .mileageUpdate(mileageUpdate)
                .currentPoint(currentPayMileage)
                .updateDate(MileageTimeSetter.currentDateTime())
                .build();
        mileageRepository.save(mileageEarningUsage);
    }

    private MileagePaymentUpdate saveMileagePaymentUpdate(Payment payment, long currentPayMileage, MileageUpdate savedMileageUpdate, MileagePaymentReason reason) {
        MileagePaymentUpdate mileagePaymentUpdate = MileagePaymentUpdate.builder()
                .mileageUpdate(savedMileageUpdate)
                .payment(payment)
                .updatePoint(currentPayMileage)
                .paymentReason(reason)
                .build();

        return mileageRepository.save(mileagePaymentUpdate);
    }

    @Override
    public void recoveryMileage(Mileage mileage, Payment payment) {
//        long totalPlusPoint = 0L;
//        MileagePaymentUpdate paymentMileage = mileageRepository.findMileageByPayment(payment);
//        List<MileageTransactionUsage> transactionUsageMileageList = mileageRepository.findTransactionUsageMileageByPaymentMileage(paymentMileage);
//        for (MileageTransactionUsage mileageTransactionUsage : transactionUsageMileageList) {
//            MileageEarningUsage expiredMileage = mileageRepository.findExpiredMileage(mileageTransactionUsage);
//            LocalDateTime expireDate = expiredMileage.getMileageUpdate().getExpireDate();
//            long usedPoint = mileageTransactionUsage.getUsedPoint();
//
//            expiredMileage.updateCurrentPoint(usedPoint);
//            expiredMileage.updateCurrendDate(MileageTimeSetter.currentDateTime());
//
//            MileageUpdate mileageUpdate = MileageUpdate.builder()
//                    .updateDate(MileageTimeSetter.currentDateTime())
//                    .expireDate(expireDate)
//                    .statusType(MileageStatusType.EARNING)
//                    .build();
//            MileageUpdate saveMileageUpdate = mileageRepository.save(mileageUpdate);
//            MileagePaymentUpdate mileagePaymentUpdate = MileagePaymentUpdate.builder()
//                    .mileageUpdate(saveMileageUpdate)
//                    .updatePoint(usedPoint)
//                    .payment(payment)
//                    .paymentReason(MileagePaymentReason.REFUND)
//                    .build();
//            mileageRepository.save(mileagePaymentUpdate);
//            mileageRepository.save(expiredMileage);
//            totalPlusPoint += usedPoint;
//        }
//
//        mileage.addPoint(totalPlusPoint);
//        mileageRepository.save(mileage);
    }

    @Override
    public void payingMileage(Payment payment) {
        User user = payment.getReservation().getUser();
        Mileage findMileage = user.getMileage();
        long payMileage = payment.getPayMileage();
        MileageUpdate savedUpdateMileage = saveMileageUpdate(findMileage, payMileage, MileageStatusType.USE);
        MileagePaymentUpdate savedPaymentMileage = saveMileagePaymentUpdate(payment, payMileage, savedUpdateMileage, MileagePaymentReason.USE_MILEAGE);

        List<MileageEarningUsage> mileageEarningUsageList = mileageRepository.findAllMileageEarningUsageByMileageUpdate(savedUpdateMileage);
        long remainMileagePoint = payMileage;
        for (MileageEarningUsage update : mileageEarningUsageList) {
            Long updatePoint = update.getCurrentPoint();
            if (updatePoint > 0) {
                long currentPoint = 0L;
                long minusPoint = updatePoint;
                if (updatePoint <= remainMileagePoint) {
                    remainMileagePoint -= updatePoint;
                } else {
                    minusPoint = remainMileagePoint;
                    remainMileagePoint = 0;
                }
                saveAndUpdateMileage(savedPaymentMileage, update, minusPoint);
            }
            if (remainMileagePoint == 0) {
                break;
            }
        }
        findMileage.minusPoint(payMileage);
    }

    private void saveAndUpdateMileage(MileagePaymentUpdate mileageByPayment, MileageEarningUsage earningUsage, Long minusPoint) {
        earningUsage.minusPoint(minusPoint);
        earningUsage.updateCurrentDate(MileageTimeSetter.currentDateTime());
        MileageTransactionUsage mileageTransactionUsage = MileageTransactionUsage.builder()
                .mileagePaymentUpdate(mileageByPayment)
                .mileageEarningUsage(earningUsage)
                .usedPoint(minusPoint)
                .build();
        mileageRepository.save(mileageTransactionUsage);
    }

    private Long getLatestUpdatePoint(MileageUpdate update) {
        Long updatePoint = update.getUpdatePoint();
        List<MileageEarningUsage> mileageExpiredUpdateList = update.getMileageExpiredUpdateList();
        if (mileageExpiredUpdateList.size() != 0) {
            // CAN USE LATEST MILEAGE?
            MileageEarningUsage result = mileageExpiredUpdateList.get(mileageExpiredUpdateList.size() - 1);
            updatePoint = result.getCurrentPoint();
        }
        return updatePoint;
    }
}
