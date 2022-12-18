package com.golfzonaca.officesharingplatform.repository.payment;

import com.golfzonaca.officesharingplatform.domain.Payment;
import com.golfzonaca.officesharingplatform.domain.Reservation;

import java.util.List;

public interface PaymentRepository {

    Payment save(Payment payment);

    List<Payment> findByReservationId(long reservationId); // why ? 파라미터를 Payment 객체로 넣게되면 Payment를 찾아오는 메소드를 또 만들어야하기때문이라 생각했음 (기능이 중복되기 때문에)

    Payment findById(long paymentId);

    List<Payment> findProgressingPaymentByReservation(Reservation reservation);

    void delete(Payment payment);

    List<Payment> findByReservationAndProgressingStatus(Reservation reservation);
}
