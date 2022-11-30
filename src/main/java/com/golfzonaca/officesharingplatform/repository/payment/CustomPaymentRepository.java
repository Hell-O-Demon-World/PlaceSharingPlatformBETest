package com.golfzonaca.officesharingplatform.repository.payment;

import com.golfzonaca.officesharingplatform.domain.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

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
    public List<Payment> getCancelInfo(long reservationId) {
        return queryPaymentRepository.getCancelInfo(reservationId);
    }
}
