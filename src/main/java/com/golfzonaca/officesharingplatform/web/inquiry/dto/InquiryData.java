package com.golfzonaca.officesharingplatform.web.inquiry.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InquiryData {
    @NotBlank
    private String title;
    @NotBlank
    private String question;
}
