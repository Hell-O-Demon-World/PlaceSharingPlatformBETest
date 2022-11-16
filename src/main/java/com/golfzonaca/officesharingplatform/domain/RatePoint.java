package com.golfzonaca.officesharingplatform.domain;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
public class RatePoint {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "RATING_POINT", nullable = false)
    private Float ratingPoint;
}
