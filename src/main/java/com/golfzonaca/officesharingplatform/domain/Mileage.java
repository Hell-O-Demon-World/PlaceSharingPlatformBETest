package com.golfzonaca.officesharingplatform.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity
@NoArgsConstructor
public class Mileage {
    @Id
    @GeneratedValue
    private long id;
    private int point;
}
