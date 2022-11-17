package com.golfzonaca.officesharingplatform.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(name = "ADDRESS", columnNames = "ADDRESS")})
@NoArgsConstructor
public class Address {

    @Id
    @GeneratedValue
    private Long id;
    @Column(name = "ADDRESS", unique = true, nullable = false, length = 100)
    private String address;
    @Column(name = "POSTALCODE", nullable = false, length = 5)
    private String postalCode;

    public Address(Long id, String address, String postalCode) {
        this.id = id;
        this.address = address;
        this.postalCode = postalCode;
    }
}
