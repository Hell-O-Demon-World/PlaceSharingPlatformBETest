package com.golfzonaca.officesharingplatform.service.refund;

import com.golfzonaca.officesharingplatform.domain.Payment;
import com.golfzonaca.officesharingplatform.domain.Refund;
import com.golfzonaca.officesharingplatform.domain.type.PayType;
import com.golfzonaca.officesharingplatform.repository.refund.RefundRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class SpringJpaRefundService implements RefundService {

    private final RefundRepository refundRepository;

    @Override
    public List<Refund> processingRefundData(List<Payment> findPayment) {

        List<Refund> refundData = new LinkedList<>();

        for (Payment payment : findPayment) {

            int calculateRefund = (int) (payment.getPrice());

            if (payment.getType().equals(PayType.FULLPAYMENT)) {
                calculateRefund = (int) (payment.getPrice() * 0.8);
            }
            Refund refund = Refund.builder()
                    .payment(payment)
                    .refundDateTime(LocalDateTime.now())
                    .refundPrice(calculateRefund)
                    .refundMileage(payment.getPayMileage())
                    .recallMileage(payment.getSavedMileage())
                    .refundStatus(false)
                    .build();
            refundRepository.save(refund);
            refundData.add(refund);
        }
        return refundData;
    }
}
