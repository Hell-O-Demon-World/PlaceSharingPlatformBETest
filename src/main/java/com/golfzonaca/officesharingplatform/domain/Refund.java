package com.golfzonaca.officesharingplatform.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "REFUND")
@NoArgsConstructor
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
}
