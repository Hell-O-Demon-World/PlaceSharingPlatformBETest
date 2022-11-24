package com.golfzonaca.officesharingplatform.web.inquiry.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InquirySaveData {
    @NotBlank
    private String title;
    @NotBlank
    private String question;
    @NotBlank
    private String dateTime;
    @NotBlank
    private String status;
}
