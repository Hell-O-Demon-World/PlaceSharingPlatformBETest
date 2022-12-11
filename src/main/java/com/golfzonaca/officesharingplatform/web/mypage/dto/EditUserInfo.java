package com.golfzonaca.officesharingplatform.web.mypage.dto;

import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
public class EditUserInfo {
    private String password;
    private String tel;
    private String job;
    private Map<String, Boolean> preferType = new LinkedHashMap<>();
}
