package com.golfzonaca.officesharingplatform.service.mileage;

import com.golfzonaca.officesharingplatform.domain.*;
import com.golfzonaca.officesharingplatform.domain.type.MileagePaymentReason;
import com.golfzonaca.officesharingplatform.domain.type.MileageStatusType;
import com.golfzonaca.officesharingplatform.repository.mileage.MileageRepository;
import com.golfzonaca.officesharingplatform.service.payment.MileageSupporter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class SpringJpaDslMileageService implements MileageService {
    private final MileageRepository mileageRepository;
    private final static long INITIAL_POINT = 0;

    @Override
    public Mileage join() {
        Mileage mileage = new Mileage(INITIAL_POINT);
        mileageRepository.save(mileage);
        MileageUpdate mileageUpdate = MileageUpdate.builder()
                .mileage(mileage)
                .updateDate(LocalDateTime.now())
                .statusType(MileageStatusType.NEW_MEMBER)
                .build();
        mileageRepository.save(mileageUpdate);
        return mileage;
    }

    @Override
    public void savingFullPaymentMileage(Payment payment) {
        Mileage mileage = payment.getReservation().getUser().getMileage();
        long currentPayMileage = payment.getSavedMileage();
        MileageUpdate mileageUpdate = MileageUpdate.builder()
                .mileage(mileage)
                .statusType(MileageStatusType.EARNING)
                .updateDate(MileageSupporter.currentDateTime())
                .expireDate(MileageSupporter.expiredDateTime())
                .build();
        mileageRepository.save(mileageUpdate);
        MileagePaymentUpdate mileagePaymentUpdate = MileagePaymentUpdate.builder()
                .mileageUpdate(mileageUpdate)
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
            expiredMileage.updateCurrendDate(MileageSupporter.currentDateTime());

            MileageUpdate mileageUpdate = MileageUpdate.builder()
                    .updateDate(MileageSupporter.currentDateTime())
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
        Mileage findMileage = mileageRepository.findByUser(payment.getReservation().getUser());
        long payMileage = payment.getPayMileage();
        MileageUpdate mileageUpdate = MileageUpdate.builder()
                .mileage(findMileage)
                .statusType(MileageStatusType.USE)
                .updatePoint(payment.getSavedMileage())
                .updateDate(MileageSupporter.currentDateTime())
                .expireDate(MileageSupporter.expiredDateTime())
                .build();
        MileageUpdate savedUpdateMileage = mileageRepository.save(mileageUpdate);
        MileagePaymentUpdate mileageByPayment = MileagePaymentUpdate.builder()
                .payment(payment)
                .paymentReason(MileagePaymentReason.USE_MILEAGE)
                .updatePoint(payMileage)
                .mileageUpdate(savedUpdateMileage)
                .build();
        MileagePaymentUpdate savedPaymentMileage = mileageRepository.save(mileageByPayment);

        List<MileageUpdate> mileageUpdateAll = mileageRepository.findMileageUpdateAllLikeUserAndExpireDate(user.getMileage(), MileageSupporter.currentDateTime());
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
                .updateDate(MileageSupporter.currentDateTime())
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
