package com.golfzonaca.officesharingplatform.domain;

import lombok.Data;

import java.util.List;

@Data
public class User {
    private Long id;
    private String userName;
    private String userMail;
    private String userPw;
    private Long mileageId;
    private String userTel;
    private String userJob;
    private String userPlace;

    public User(String userName, String userMail, String userPw, String userTel, String userJob, String userPlace) {
        this.userName = userName;
        this.userMail = userMail;
        this.userPw = userPw;
        this.userTel = userTel;
        this.userJob = userJob;
        this.userPlace = userPlace;
    }
}
