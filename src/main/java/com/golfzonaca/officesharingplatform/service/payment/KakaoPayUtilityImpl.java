package com.golfzonaca.officesharingplatform.service.payment;

import com.golfzonaca.officesharingplatform.domain.Reservation;
import com.golfzonaca.officesharingplatform.domain.type.PayType;
import com.golfzonaca.officesharingplatform.domain.type.PayWay;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
public class KakaoPayUtilityImpl implements KakaoPayUtility {

    @Override
    public Integer calculateTotalAmount(Reservation reservation, String payWay, String payType) {

        Integer totalAmount = ((reservation.getResEndTime().getHour()) - (reservation.getResStartTime().getHour())) * (reservation.getRoom().getRoomKind().getPrice());

        if (reservation.getRoom().getRoomKind().getRoomType().contains("OFFICE")) {
            return Math.toIntExact((ChronoUnit.DAYS.between(reservation.getResEndDate(), reservation.getResStartDate()) * reservation.getRoom().getRoomKind().getPrice()));
        } else {
            if (payWay.equals(PayWay.PREPAYMENT)) {
                if (payType.equals(PayType.DEPOSIT)) {
                    totalAmount = (int) (totalAmount * 0.2);
                }
            } else {
                totalAmount = (int) (totalAmount * 0.8);
            }
        }
        return totalAmount;
    }

    @Override
    public Integer calculateTaxFreeAmount(Integer totalAmount) {
        return (totalAmount) * 10 / 11;
    }

    @Override
    public Integer calculateVatAmount(Integer totalAmount) {
        return totalAmount / 11;
    }

    @Override
    public long calculateMileage(Integer totalAmount) {
        return (long) (totalAmount * 0.05);
    }
}
