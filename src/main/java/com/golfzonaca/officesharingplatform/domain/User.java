package com.golfzonaca.officesharingplatform.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "USER")
@Data
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "USER_NAME", nullable = false)
    private String username;
    @Column(name = "USER_MAIL", nullable = false, unique = true)
    private String email;
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

    public User(String username, String email, String password, String phoneNumber, String job, String userPlace) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.job = job;
        this.userPlace = userPlace;
    }
}
