package com.golfzonaca.officesharingplatform.domain;

import com.golfzonaca.officesharingplatform.domain.type.PayStatus;
import com.golfzonaca.officesharingplatform.domain.type.PayType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

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

    @Builder
    public Payment(User user, Room room, LocalDateTime payDateTime, long price, PayStatus status, long savedMileage, PayType type, String apiCode) {
        this.user = user;
        this.room = room;
        this.payDateTime = payDateTime;
        this.price = price;
        this.status = status;
        this.savedMileage = savedMileage;
        this.type = type;
        this.apiCode = apiCode;
    }
}
