package com.golfzonaca.officesharingplatform.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
public class Mileage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "POINT", nullable = false, length = 11)
    private long point;
}
