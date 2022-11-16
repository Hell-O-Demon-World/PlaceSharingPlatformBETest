package com.golfzonaca.officesharingplatform.domain;

import lombok.Data;

@Data
public class Address {

    private Long id;

    private String address;

    private String postalCode;

    public Address() {
    }

    public Address(Long id, String address, String postalCode) {
        this.id = id;
        this.address = address;
        this.postalCode = postalCode;
    }
}
