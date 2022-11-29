package com.golfzonaca.officesharingplatform.domain;

import com.golfzonaca.officesharingplatform.domain.type.PG;
import com.golfzonaca.officesharingplatform.domain.type.PayStatus;
import com.golfzonaca.officesharingplatform.domain.type.PayType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(name = "Payment", columnNames = {"PAY_API_CODE"})})
@NoArgsConstructor
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
    private LocalDate payDate;
    @Column(name = "PAY_TIME", nullable = false)
    private LocalTime payTime;

    @Column(name = "PAY_PRICE", nullable = false)
    private long price; //마일리지를 제외한 돈 나가는거

    @Column(name = "PAY_STATUS", nullable = false)
    @Enumerated(EnumType.STRING)
    private PayStatus status;

    @Column(name = "PAY_MILEAGE", nullable = false)
    private long savedMileage; //사용한 마일리지

    @Column(name = "PAY_TYPE", nullable = false)
    @Enumerated(EnumType.STRING)
    private PayType type;

    @Column(name = "PAY_API_CODE", nullable = false)
    private String apiCode; //결제하면 주문번호 튀어나오는거

    @Column(name = "PG", nullable = false)
    @Enumerated(EnumType.STRING)
    private PG pg;

    @Builder
    public Payment(User user, Room room, LocalDate payDate, LocalTime payTime, long price, PayStatus status, long savedMileage, PayType type, String apiCode, PG pg) {
        this.user = user;
        this.room = room;
        this.payDate = payDate;
        this.payTime = payTime;
        this.price = price;
        this.status = status;
        this.savedMileage = savedMileage;
        this.type = type;
        this.apiCode = apiCode;
        this.pg = pg;
    }
}
