package com.golfzonaca.officesharingplatform.web.main.dto.request;

import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
public class RequestMailForm {
    @NotNull(message = "이메일은 Null일 수 없습니다.")
    @Email(message = "이메일 형식을 지켜주세요.")
    @Size(max = 32, message = "이메일 길이는 최대 32자 이내 입니다.")
    private String email;
}
