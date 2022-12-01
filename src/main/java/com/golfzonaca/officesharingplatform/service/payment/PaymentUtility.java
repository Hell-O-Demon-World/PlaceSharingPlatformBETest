package com.golfzonaca.officesharingplatform.service.payment;

import com.golfzonaca.officesharingplatform.domain.Payment;
import com.golfzonaca.officesharingplatform.domain.Reservation;
import com.golfzonaca.officesharingplatform.domain.RoomKind;
import com.golfzonaca.officesharingplatform.domain.type.PayWay;

import java.time.LocalDate;
import java.time.LocalTime;

public interface PaymentUtility {
    /*
     * 1. 예약 유무 확인 -> 예약되어져 있니?
     * 2. 결제 타입(Pay way) 체크 -> 결제타입
     * 3. 마일리지 계산 -> 마일리지의 데이터 타입
     * 4. 잔금 계산 -> 잔금 데이터 타입
     * 5. 보증금 계산 -> 보증금 데이터 타입
     * 6. 환불금액 계산 -> 금액 데ㅔ이터 타입
     * */
    boolean checkReservation(long reservationId);

    PayWay checkPayWay(String payWay);

    long calculatePoint(long totalAmount);

    long calculateBalance(long totalAmount);

    long calculateDeposit(long totalAmount);

    long calculateRefund(Payment payment, long totalAmount);

    int convertPaymentDateTimeToSecond(LocalDate paymentDate, LocalTime paymentTime);

    long calculateTotalAmount(Reservation reservation, RoomKind roomKind);
}
