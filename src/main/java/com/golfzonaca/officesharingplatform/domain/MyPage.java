package com.golfzonaca.officesharingplatform.domain;

import com.golfzonaca.officesharingplatform.web.mypage.form.MyPageReservationForm;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MyPage {
    private String userName;
    private List<MyPageReservationForm> myPageReservationList;
}
