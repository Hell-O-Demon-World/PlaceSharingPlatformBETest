package com.golfzonaca.officesharingplatform.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public class Company {

    private long id;
    private String companyLoginId;
    private String companyPw;
    private String companyTel;
    private String companyRegNum;
    private String companyRepName;
    private Address addressId;
}
