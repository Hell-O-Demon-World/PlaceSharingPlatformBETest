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

    public long addPoint(long addPoint) {
        this.point = this.point + addPoint;
        return point;
    }

    public long minusPoint(long minusPoint) {
        this.point = this.point - minusPoint;
        return point;
    }
}
