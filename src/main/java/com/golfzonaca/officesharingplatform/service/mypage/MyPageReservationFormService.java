package com.golfzonaca.officesharingplatform.service.mypage;

import com.golfzonaca.officesharingplatform.web.mypage.form.MyPageReservationForm;

import java.util.List;

public interface MyPageReservationFormService {
    List<MyPageReservationForm> getMyPageReservationListByUserId(long userId);

}
