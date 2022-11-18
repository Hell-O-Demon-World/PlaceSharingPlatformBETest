package com.golfzonaca.officesharingplatform.domain;

import com.golfzonaca.officesharingplatform.web.mypage.form.MyPageReservationForm;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MyPage {
    private String userName;
    private List<MyPageReservationForm> myPageReservationList;
}
