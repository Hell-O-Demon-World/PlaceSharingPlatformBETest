package com.golfzonaca.officesharingplatform.service.refund;

import com.golfzonaca.officesharingplatform.domain.Payment;
import com.golfzonaca.officesharingplatform.domain.Refund;

import java.util.List;

public interface RefundService {

    List<Refund> processingRefundData(List<Payment> findPayment);
}
