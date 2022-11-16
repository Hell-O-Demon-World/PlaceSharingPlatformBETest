package com.golfzonaca.officesharingplatform.repository.mybatis.dto;

import com.golfzonaca.officesharingplatform.web.auth.form.prefertype.PreferType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UserInfoDto {
    private String userTel;
    private String userJob;
    private List<String> preferType;
}
