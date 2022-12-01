package com.golfzonaca.officesharingplatform.web.exception.form;

import lombok.Data;

@Data
public class ExceptionResponseForm {
    private final String status;
    private final String code;
    private final String message;
}
