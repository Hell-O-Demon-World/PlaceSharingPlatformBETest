package com.golfzonaca.officesharingplatform.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "REFUND")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Refund {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "PAYMENT_ID")
    private Payment payment;

    @Column(name = "REFUND_DATETIME", nullable = false)
    private LocalDateTime refundDateTime;

    @Column(name = "REFUND_PRICE", nullable = false)
    private long refundPrice;

    @Column(name = "REFUND_MILEAGE", nullable = false)
    private long refundMileage;

    @Column(name = "RECALL_MILEAGE", nullable = false)
    private long recallMileage;

    @Column(name = "REFUND_STATUS", nullable = false)
    private boolean refundStatus;

    public void updateRefundStatus(boolean refundStatus) {
        this.refundStatus = refundStatus;
    }
}
