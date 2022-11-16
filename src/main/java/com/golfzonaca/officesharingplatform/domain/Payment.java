package com.golfzonaca.officesharingplatform.domain;

import com.golfzonaca.officesharingplatform.domain.type.PayStatus;
import com.golfzonaca.officesharingplatform.domain.type.PayType;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;
    @OneToOne
    @JoinColumn(name = "ROOM_ID")
    private Room room;

    @Column(name = "PAY_DATE", nullable = false)
    private LocalDateTime payDateTime;
    @Column(name = "PAY_PRICE", nullable = false)
    private long price;
    @Column(name = "PAY_STATUS", nullable = false)
    @Enumerated(EnumType.STRING)
    private PayStatus status;
    @Column(name = "PAY_MILEAGE", nullable = false)
    private long savedMileage;
    @Column(name = "PAY_TYPE", nullable = false)
    @Enumerated(EnumType.STRING)
    private PayType type;
    @Column(name = "PAY_API_CODE", nullable = false)
    private String apiCode;
}
