package com.golfzonaca.officesharingplatform.web.mypage.form;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MyPageReservationForm {
    private String resDate;
    private String placeName;
    private String roomKind;
    private String resTime;
}
