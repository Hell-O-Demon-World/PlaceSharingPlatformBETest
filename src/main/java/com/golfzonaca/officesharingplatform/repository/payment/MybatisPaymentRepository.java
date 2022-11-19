package com.golfzonaca.officesharingplatform.repository.payment;

import com.golfzonaca.officesharingplatform.domain.Payment;
import com.golfzonaca.officesharingplatform.repository.mybatis.PaymentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MybatisPaymentRepository implements PaymentRepository {

    private final PaymentMapper paymentMapper;

    @Override
    public void save(Payment payment) {
        paymentMapper.save(payment);
    }
}
