package com.golfzonaca.officesharingplatform.web.payment.form;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class KakaoPayReady {

    //response
    private String tid, next_redirect_pc_url;
    private LocalDateTime created_at;
}
