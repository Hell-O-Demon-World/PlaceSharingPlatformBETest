package com.golfzonaca.officesharingplatform.controller.main.dto.request;

import com.golfzonaca.officesharingplatform.annotation.PhoneNum;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
public class PwCheckForm {
    @NotNull(message = "이메일은 Null일 수 없습니다.")
    @Email(message = "이메일 형식을 지켜주세요.")
    @Size(max = 32, message = "이메일 길이는 최대 32자 이내 입니다.")
    private String email;
    @PhoneNum(message = "전화번호는 '-' 없이 숫자만 입력해주시기 바랍니다.")
    @Size(min = 9, max = 11, message = "전화번호의 길이는 최소 9자, 최대 11자 입니다.")
    private String tel;
    @NotBlank(message = "비밀번호에 빈 문자나 공백 또는 Null 값이 들어갈 수 없습니다.")
    @Size(min = 8, max = 15, message = "비밀번호의 길이는 8 ~ 15 자리여야 합니다.")
    private String password;
    @NotBlank(message = "비밀번호에 빈 문자나 공백 또는 Null 값이 들어갈 수 없습니다.")
    @Size(min = 8, max = 15, message = "비밀번호의 길이는 8 ~ 15 자리여야 합니다.")
    private String checkPassword;
}
