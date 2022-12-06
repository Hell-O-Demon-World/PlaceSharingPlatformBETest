package com.golfzonaca.officesharingplatform.repository.payment;

import com.golfzonaca.officesharingplatform.domain.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Repository
@Transactional
@RequiredArgsConstructor
public class CustomPaymentRepository implements PaymentRepository {

    private final SpringJpaPaymentRepository jpaRepository;
    private final QueryPaymentRepository queryPaymentRepository;


    @Override
    public void save(Payment payment) {
        jpaRepository.save(payment);
    }

    @Override
    public List<Payment> findByReservationId(long reservationId) {
        return queryPaymentRepository.findByReservationId(reservationId);
    }

    @Override
    public Payment findById(long paymentId) {
        return jpaRepository.findById(paymentId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 결제정보입니다."));
    }

}
