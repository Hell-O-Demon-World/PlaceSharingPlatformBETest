package com.golfzonaca.officesharingplatform.domain;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "ratepoint")
@NoArgsConstructor
public class RatePoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "RATINGPOINT", nullable = false)
    private float ratingPoint;

    @Builder
    public RatePoint(float ratingPoint) {
        this.ratingPoint = ratingPoint;
    }

    public void UpdateRatePoint(float ratingPoint) {
        this.ratingPoint = ratingPoint;
    }

}
