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
    @ManyToOne
    @JoinColumn(name = "MILEAGE_ID")
    private Mileage mileage;
    @Column(name = "room_id")
    private Long roomid;
    @Column(name = "room_kind_id")
    private Long roomkindId;
    @Column(name = "place_id")
    private Long placeId;

    @Column(name = "pay_date", nullable = false)
    private LocalDateTime payDateTime;
    @Column(name = "pay_price", nullable = false)
    private long price;
    @Column(name = "pay_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private PayStatus status;
    @Column(name = "pay_mileage", nullable = false)
    private long savedMileage;
    @Column(name = "pay_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private PayType type;
    @Column(name = "pay_api_code", nullable = false)
    private String apiCode;
}
