package com.golfzonaca.officesharingplatform.service.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignUpInfoDto {
    private String email;
    private String password;
    private String name;
    private String phoneNumber;
    private String job;
    private String userPreferType;
}
