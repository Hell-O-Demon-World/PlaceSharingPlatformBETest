package com.golfzonaca.officesharingplatform.domain;

import com.golfzonaca.officesharingplatform.domain.type.PayType;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
public class Payment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;
    @OneToOne
    @JoinColumn(name = "ROOM_ID")
    private Room room;

    @Column(name = "trx_date")
    private LocalDate trxDate;
    private int companyId;
    private int roomId;
    private int trxPrice;
    private boolean trxStatus;
    private int trxMileage;
    private PayType trxType;
    private String trxApiCode;
}
