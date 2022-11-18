package com.golfzonaca.officesharingplatform.domain;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
public class RatePoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "RATING_POINT", nullable = false)
    private float ratingPoint;

    @Builder
    public RatePoint(float ratingPoint) {
        this.ratingPoint = ratingPoint;
    }
}
