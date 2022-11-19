package com.golfzonaca.officesharingplatform.repository.payment;

import com.golfzonaca.officesharingplatform.domain.Payment;

import java.time.LocalDateTime;

public interface PaymentRepository {

    void save(Payment payment);

}
