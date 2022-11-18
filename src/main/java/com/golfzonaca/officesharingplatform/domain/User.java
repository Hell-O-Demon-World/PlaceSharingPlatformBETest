package com.golfzonaca.officesharingplatform.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
public class User {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "USER_NAME", nullable = false)
    private String name;
    @Column(name = "USER_MAIL", nullable = false, unique = true)
    private String mail;
    @Column(name = "USER_PW", nullable = false)
    private String password;
    @Column(name = "USER_TEL", nullable = false)
    private String phoneNumber;
    @Column(name = "USER_JOB", nullable = false)
    private String job;
    @Column(name = "USER_PLACE", nullable = false)
    private String userPlace;
    @OneToOne
    @JoinColumn(name = "MILEAGE_ID")
    private Mileage mileage;

    public User(String name, String mail, String password, String phoneNumber, String job, String userPlace) {
        this.name = name;
        this.mail = mail;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.job = job;
        this.userPlace = userPlace;
    }
}
