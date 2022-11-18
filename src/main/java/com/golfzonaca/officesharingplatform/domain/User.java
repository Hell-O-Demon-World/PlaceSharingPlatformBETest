package com.golfzonaca.officesharingplatform.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "USER", uniqueConstraints = {@UniqueConstraint(name = "USER", columnNames = {"USER_MAIL", "USER_TEL", "MILEAGE_ID"})})
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

    @Builder
    public User(String username, String email, String password, String phoneNumber, String job, String userPlace, Mileage mileage) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.job = job;
        this.userPlace = userPlace;
        this.mileage = mileage;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setMileage(Mileage mileage) {
        this.mileage = mileage;
    }
}
