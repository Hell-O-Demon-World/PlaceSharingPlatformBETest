package com.golfzonaca.officesharingplatform.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
public class Mileage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "POINT", nullable = false, length = 11)
    private long point;

    @Builder
    public Mileage(long point) {
        this.point = point;
    }

    public long addPoint(long userPoint, long addPoint) {
        this.point = userPoint + addPoint;
        return point;
    }

    public long minusPoint(long userPoint, long minusPoint) {
        this.point = userPoint - minusPoint;
        return point;
    }
}
