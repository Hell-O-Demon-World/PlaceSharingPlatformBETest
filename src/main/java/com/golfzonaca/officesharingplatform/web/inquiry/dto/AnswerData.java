package com.golfzonaca.officesharingplatform.web.inquiry.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AnswerData {
    @NotBlank
    private String answer;
}
