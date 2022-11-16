package com.golfzonaca.officesharingplatform.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity
@NoArgsConstructor
public class Address {

    @Id
    @GeneratedValue
    @Column(unique = true, nullable = false)
    private Long id;
    @Column(unique = true, nullable = false, length = 100)
    private String address;
    @Column(nullable = false, length = 5)
    private String postalCode;

    public Address(Long id, String address, String postalCode) {
        this.id = id;
        this.address = address;
        this.postalCode = postalCode;
    }
}
