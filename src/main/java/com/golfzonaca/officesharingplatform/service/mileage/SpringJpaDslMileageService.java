package com.golfzonaca.officesharingplatform.service.mileage;

import com.golfzonaca.officesharingplatform.domain.*;
import com.golfzonaca.officesharingplatform.domain.type.MileagePaymentReason;
import com.golfzonaca.officesharingplatform.domain.type.MileageStatusType;
import com.golfzonaca.officesharingplatform.repository.mileage.MileageRepository;
import com.golfzonaca.officesharingplatform.service.payment.MileageTimeSetter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
        log.info("saving mileage...");
        Mileage mileage = payment.getReservation().getUser().getMileage();
        long currentPayMileage = payment.getSavedMileage();
        MileageUpdate mileageUpdate = MileageUpdate.builder()
                .mileage(mileage)
                .statusType(MileageStatusType.EARNING)
                .updatePoint(currentPayMileage)
                .updateDate(MileageTimeSetter.currentDateTime())
                .expireDate(MileageTimeSetter.expiredDateTime())
                .build();
        MileageUpdate savedMileageUpdate = mileageRepository.save(mileageUpdate);
        MileagePaymentUpdate mileagePaymentUpdate = MileagePaymentUpdate.builder()
                .mileageUpdate(savedMileageUpdate)
                .payment(payment)
                .updatePoint(currentPayMileage)
                .paymentReason(MileagePaymentReason.FULL_PAYMENT)
                .build();
        mileageRepository.save(mileagePaymentUpdate);
        mileage.addPoint(currentPayMileage);
    }

    @Override
    public void recoveryMileage(Mileage mileage, Payment payment) {
        long totalPlusPoint = 0L;
        MileagePaymentUpdate paymentMileage = mileageRepository.findMileageByPayment(payment);
        List<MileageTransactionUsage> transactionUsageMileageList = mileageRepository.findTransactionUsageMileageByPaymentMileage(paymentMileage);
        for (MileageTransactionUsage mileageTransactionUsage : transactionUsageMileageList) {
            MileageExpiredHistory expiredMileage = mileageRepository.findExpiredMileage(mileageTransactionUsage);
            LocalDateTime expireDate = expiredMileage.getMileageUpdate().getExpireDate();
            long usedPoint = mileageTransactionUsage.getPoint();

            expiredMileage.updateCurrentPoint(usedPoint);
            expiredMileage.updateCurrendDate(MileageTimeSetter.currentDateTime());

            MileageUpdate mileageUpdate = MileageUpdate.builder()
                    .updateDate(MileageTimeSetter.currentDateTime())
                    .expireDate(expireDate)
                    .statusType(MileageStatusType.EARNING)
                    .build();
            MileageUpdate saveMileageUpdate = mileageRepository.save(mileageUpdate);
            MileagePaymentUpdate mileagePaymentUpdate = MileagePaymentUpdate.builder()
                    .mileageUpdate(saveMileageUpdate)
                    .updatePoint(usedPoint)
                    .payment(payment)
                    .paymentReason(MileagePaymentReason.REFUND)
                    .build();
            mileageRepository.save(mileagePaymentUpdate);
            mileageRepository.save(expiredMileage);
            totalPlusPoint += usedPoint;
        }

        mileage.addPoint(totalPlusPoint);
        mileageRepository.save(mileage);
    }

    @Override
    public void payingMileage(Payment payment) {
        User user = payment.getReservation().getUser();
        Mileage findMileage = user.getMileage();
        long payMileage = payment.getPayMileage();
        MileageUpdate mileageUpdate = MileageUpdate.builder()
                .mileage(findMileage)
                .statusType(MileageStatusType.USE)
                .updatePoint(payment.getSavedMileage())
                .updateDate(MileageTimeSetter.currentDateTime())
                .expireDate(MileageTimeSetter.expiredDateTime())
                .build();
        MileageUpdate savedUpdateMileage = mileageRepository.save(mileageUpdate);
        MileagePaymentUpdate mileageByPayment = MileagePaymentUpdate.builder()
                .payment(payment)
                .paymentReason(MileagePaymentReason.USE_MILEAGE)
                .updatePoint(payMileage)
                .mileageUpdate(savedUpdateMileage)
                .build();
        MileagePaymentUpdate savedPaymentMileage = mileageRepository.save(mileageByPayment);
        
        List<MileageUpdate> mileageUpdateAll = mileageRepository.findMileageUpdateAllLikeUserAndExpireDate(findMileage, MileageTimeSetter.currentDateTime());
        long remainMileagePoint = payMileage;
        while (remainMileagePoint > 0) {
            for (MileageUpdate update : mileageUpdateAll) {
                Long updatePoint = getLatestUpdatePoint(update);
                long currentPoint = 0L;
                Long minusPoint = updatePoint;
                if (updatePoint <= remainMileagePoint) {
                    remainMileagePoint -= updatePoint;
                } else {
                    minusPoint = remainMileagePoint;
                    currentPoint = updatePoint - remainMileagePoint;
                    remainMileagePoint = 0;
                }
                saveAndUpdateMileage(savedPaymentMileage, update, currentPoint, minusPoint);
            }
        }
        findMileage.minusPoint(payMileage);
        mileageRepository.save(findMileage);
    }

    private void saveAndUpdateMileage(MileagePaymentUpdate mileageByPayment, MileageUpdate update, Long currentPoint, Long minusPoint) {
        update.minusPoint(minusPoint);
        mileageRepository.save(update);
        MileageTransactionUsage mileageTransactionUsage = MileageTransactionUsage.builder()
                .mileagePaymentUpdate(mileageByPayment)
                .point(minusPoint)
                .build();
        MileageTransactionUsage savedTransactionUsageMileage = mileageRepository.save(mileageTransactionUsage);
        MileageExpiredHistory mileageExpiredHistory = MileageExpiredHistory.builder()
                .mileageUpdate(update)
                .mileageTransactionUsage(savedTransactionUsageMileage)
                .currentPoint(currentPoint)
                .updateDate(MileageTimeSetter.currentDateTime())
                .build();
        mileageRepository.save(mileageExpiredHistory);
    }

    private Long getLatestUpdatePoint(MileageUpdate update) {
        Long updatePoint = update.getUpdatePoint();
        List<MileageExpiredHistory> mileageExpiredUpdateList = update.getMileageExpiredUpdateList();
        if (mileageExpiredUpdateList.size() != 0) {
            // CAN USE LATEST MILEAGE?
            MileageExpiredHistory result = mileageExpiredUpdateList.get(mileageExpiredUpdateList.size() - 1);
            updatePoint = result.getCurrentPoint();
        }
        return updatePoint;
    }
}
