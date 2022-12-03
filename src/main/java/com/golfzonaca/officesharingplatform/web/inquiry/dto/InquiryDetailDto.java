package com.golfzonaca.officesharingplatform.web.inquiry.dto;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class InquiryDetailDto {
    private String inquiryId;
    private String inquiryWriter;
    private String inquiryTitle;
    private String inquiryContent;
    private String inquiryDateTime;
    private String inquiryStatus;
    private String answerId;
    private String answerContent;
}
