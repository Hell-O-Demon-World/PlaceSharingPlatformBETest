package com.golfzonaca.officesharingplatform.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Getter
@ToString
@Entity
@Table(UniqueConstraint)
@NoArgsConstructor
public class Company {

    @Id
    @Column(unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "COMPANY_LOGINID", unique = true, nullable = false, length = 15)
    private String loginId;
    @Column(name = "COMPANY_PW", nullable = false, length = 15)
    private String Pw;
    @Column(name = "COMPANY_NAME", unique = true, nullable = false, length = 30)
    private String Name;
    @Column(name = "COMPANY_TEL", unique = true, nullable = false, length = 22)
    private String tel;
    @Column(name = "COMPANY_REGNUM", unique = true, nullable = false, length = 12)
    private String regNum;
    @Column(name = "COMPANY_REPNAME", nullable = false, length = 20)
    private String repName;
    @OneToOne
    @JoinColumn(name = "ADDRESS_ID")
    private Address address;
}
