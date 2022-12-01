package com.golfzonaca.officesharingplatform.service.payment;

import com.golfzonaca.officesharingplatform.domain.Payment;
import com.golfzonaca.officesharingplatform.domain.Reservation;
import com.golfzonaca.officesharingplatform.domain.RoomKind;
import com.golfzonaca.officesharingplatform.domain.type.PayWay;
import com.golfzonaca.officesharingplatform.repository.reservation.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentUtilityImpl implements PaymentUtility {

    private final ReservationRepository reservationRepository;

    @Override
    public boolean checkReservation(long reservationId) {

        if (reservationRepository.findById(reservationId) != null) {
            return true;
        }
        return false;
    }

    @Override
    public PayWay checkPayWay(String payWay) {

        if (payWay.equals(PayWay.PREPAYMENT)) {
            return PayWay.PREPAYMENT;
        }
        return PayWay.POSTPAYMENT;
    }

    @Override
    public long calculatePoint(long totalAmount) {
        return (long) (totalAmount * 0.05);
    }

    @Override
    public long calculateBalance(long totalAmount) {
        return (long) (totalAmount * 0.8);
    }

    @Override
    public long calculateDeposit(long totalAmount) {
        return (long) (totalAmount * 0.2);
    }

    @Override
    public long calculateRefund(Payment payment, long totalAmount) {
        /*
         * 예약 후 1시간 이내 취소: 전체 환불
         * 예약 후 1시간 이후 취소: 선결제금액은 환불, 보증금은 환불 X
         *
         * payDate가 한 시간이 지나지 않았으면 -> 전체 환불
         * payDate가 한 시간이 지났고 선결제이면 -> 보증금은 환불해주지않고 선결제 금액에 대해서는 환불
         * */

        // payment에서 시간으로 1시간 이내인지 비교
        // 1시간 이내라면 -> payment에서 금액 가져와서 계산
        // 1시간 이후라면 -> payment에서 금액 가져와서 계산

        int currentDayToSecond = LocalDateTime.now().getSecond();

        // 2022/12/01 오후 11:30에 결제함. 근데 2022/12/02 오전 12:10에 취소하고 싶음
        // current day랑 current time을 다 second로 고쳐

        LocalDate paymentDate = payment.getPayDate();
        LocalTime paymentTime = payment.getPayTime();

        if (Math.abs(currentDayToSecond - convertPaymentDateTimeToSecond(paymentDate, paymentTime)) < 3600) {
            return payment.getPrice();
        }

        return (long) (payment.getPrice() * 0.8);
    }

    @Override
    public int convertPaymentDateTimeToSecond(LocalDate paymentDate, LocalTime paymentTime) {
        return LocalDateTime.of(paymentDate.getYear(), paymentDate.getMonthValue(), paymentDate.getDayOfMonth(), paymentTime.getHour(), paymentTime.getMinute(), paymentTime.getSecond()).getSecond();
    }

    @Override
    public long calculateTotalAmount(Reservation reservation, RoomKind roomKind){
        return (long) (reservation.getResEndTime().getHour() - reservation.getResStartTime().getHour()) * roomKind.getPrice();
    }
}
