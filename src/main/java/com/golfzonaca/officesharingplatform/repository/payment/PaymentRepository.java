package com.golfzonaca.officesharingplatform.repository.payment;

import com.golfzonaca.officesharingplatform.domain.Payment;

public interface PaymentRepository {

    void save(Payment payment);
}
