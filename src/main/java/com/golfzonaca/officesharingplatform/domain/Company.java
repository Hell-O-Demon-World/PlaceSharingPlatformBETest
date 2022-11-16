package com.golfzonaca.officesharingplatform.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Getter
@ToString
@Entity
@NoArgsConstructor
@RequiredArgsConstructor
public class Company {

    @Id
    @Column(unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "company_loginid", unique = true, nullable = false, length = 15)
    private String loginId;
    @Column(name = "company_pw", nullable = false, length = 15)
    private String Pw;
    @Column(name = "company_name", unique = true, nullable = false, length = 30)
    private String Name;
    @Column(name = "company_tel", unique = true, nullable = false, length = 22)
    private String tel;
    @Column(name = "company_regnum", unique = true, nullable = false, length = 12)
    private String regNum;
    @Column(name = "company_repname", nullable = false, length = 20)
    private String repName;
    @Column(unique = true, nullable = false)
    private long addressId;
}
