package com.golfzonaca.officesharingplatform.domain;

import lombok.*;

import javax.persistence.*;

@Builder
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailAuthenticationCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "EMAIL", nullable = false, length = 30)
    private String email;
    @Column(name = "CODE", nullable = false, length = 4)
    private String verifyingCode;
    @Column(name = "EXPIRATION", nullable = false)
    private Boolean expiredStatus;

    public void toEntity(String email, String code, Boolean status) {
        this.email = email;
        this.verifyingCode = code;
        this.expiredStatus = status;
    }
}
